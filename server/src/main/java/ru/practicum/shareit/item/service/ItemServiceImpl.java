
package ru.practicum.shareit.item.service;

import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingsRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.mapper.CommentMapper;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.mapper.ModelMapperUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ModelMapperUtil mapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingsRepository bookingsRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        Item item = mapper.map(itemDto, Item.class);
        item.setOwner(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId))));
        item.setAvailable(true);
        item.setRequestId(item.getRequestId());

        return mapper.map(itemRepository.save(item), ItemDto.class);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %d не найдена", itemId)));
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return mapper.map(itemRepository.save(item), ItemDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String text, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        return itemRepository.findItemByText(text, page)
                .stream()
                .map(item -> mapper.map(item, ItemDto.class))
                .collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoWithBookings getItemById(Long userId, Long itemId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден!", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %d не найдена", itemId)));

        ItemDtoWithBookings itemDtoWithBookings = ItemMapper.INSTANCE.toItemDtoWithBookings(item);
        if (item.getOwner().getId() == userId) {
            ResponseBookingDto lastBooking = BookingMapper.INSTANCE.toResponseBookingDto(bookingsRepository
                    .findBookingByItemIdAndStartBeforeOrderByEndDesc(itemId, LocalDateTime.now())
                    .stream().findFirst().orElse(null));
            itemDtoWithBookings.setLastBooking(lastBooking);

            ResponseBookingDto nextBooking = BookingMapper.INSTANCE.toResponseBookingDto(bookingsRepository
                    .findBookingByItem_IdAndStartAfterAndStatusEqualsOrderByStart(itemId, LocalDateTime.now(), Status.APPROVED)
                    .stream().findFirst().orElse(null));
            itemDtoWithBookings.setNextBooking(nextBooking);
        }
        itemDtoWithBookings.setComments(new ArrayList<>());
        itemDtoWithBookings.getComments().addAll(getAllComments(itemId));
        return itemDtoWithBookings;
    }


    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoWithBookings> getAllItems(Long userId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        List<ItemDtoWithBookings> items = itemRepository.findAllByOwnerOrderById(owner, page)
                .stream()
                .map(ItemMapper.INSTANCE::toItemDtoWithBookings)
                .collect(Collectors.toList());

        return getAllItemsWithBookings(items, page);
    }

    private List<ItemDtoWithBookings> getAllItemsWithBookings(List<ItemDtoWithBookings> list, Pageable page) {
        List<Booking> bookings = bookingsRepository
                .findAll(page)
                .stream()
                .collect(Collectors.toList());
        for (ItemDtoWithBookings item : list) {
            ResponseBookingDto lastBooking = bookings
                    .stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()) && booking.getStart().isBefore(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getEnd))
                    .map(BookingMapper.INSTANCE::toResponseBookingDto)
                    .findFirst()
                    .orElse(null);
            item.setLastBooking(lastBooking);

            ResponseBookingDto nextBooking = bookings
                    .stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()) && booking.getStart()
                            .isAfter(LocalDateTime.now()) && booking.getStatus().equals(Status.APPROVED))
                    .sorted(Comparator.comparing(Booking::getStart))
                    .map(booking -> BookingMapper.INSTANCE.toResponseBookingDto(booking))
                    .findFirst()
                    .orElse(null);
            item.setNextBooking(nextBooking);
        }
        return list;
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        Comment comment = mapper.map(commentDto, Comment.class);
        comment.setCreated(LocalDateTime.now());
        Item item = itemRepository.findById(itemId).
                orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %d не найден", itemId)));
        item.getComments().add(commentDto);
        comment.setItem(item);
        comment.setAuthor(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId))));

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = bookingsRepository.findBookingsByBooker_IdAndItem_IdAndStatusEqualsAndStartBeforeAndEndBefore(userId, itemId, Status.APPROVED, now, now);

        if (bookings.isEmpty()) {
            throw new ValidationException("Данная вещь не была арендована.");
        }
        commentRepository.save(comment);

        return CommentMapper.INSTANCE.toCommentDto(comment);
    }


    private List<CommentDto> getAllComments(Long itemId) {
        return commentRepository
                .findCommentByItemId(itemId)
                .stream()
                .map(comment -> CommentMapper.INSTANCE.toCommentDto(comment))
                .collect(Collectors.toList());
    }

}


