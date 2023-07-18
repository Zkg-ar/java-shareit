
package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingsRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.mapper.CommentMapper;
import ru.practicum.shareit.mapper.ModelMapperUtil;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
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
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

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
    public List<ItemDto> search(String text,Pageable page) {
        return itemRepository.findItemByText(text,page)
                .stream()
                .map(item -> mapper.map(item, ItemDto.class))
                .collect(Collectors.toList());

    }

    @Override
    public ItemDtoWithBookings getItemById(Long userId, Long itemId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %d", itemId)));

        ItemDtoWithBookings itemDtoWithBookings = mapper.map(item, ItemDtoWithBookings.class);
        if (item.getOwner().getId() == userId) {
            ResponseBookingDto lastBooking = bookingMapper.toResponseBookingDto(bookingsRepository
                    .findBookingByItemIdAndStartBeforeOrderByEndDesc(itemId, LocalDateTime.now())
                    .stream().findFirst().orElse(null));
            itemDtoWithBookings.setLastBooking(lastBooking);

            ResponseBookingDto nextBooking = bookingMapper.toResponseBookingDto(bookingsRepository
                    .findBookingByItem_IdAndStartAfterAndStatusEqualsOrderByStart(itemId, LocalDateTime.now(), Status.APPROVED)
                    .stream().findFirst().orElse(null));
            itemDtoWithBookings.setNextBooking(nextBooking);
        }
        itemDtoWithBookings.setComments(new ArrayList<>());
        itemDtoWithBookings.getComments().addAll(getAllComments(itemId));
        return itemDtoWithBookings;
    }


    @Override
    public List<ItemDtoWithBookings> getAllItems(Long userId, Pageable page) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        List<ItemDtoWithBookings> items = itemRepository.findAllByOwnerOrderById(owner,page)
                .stream()
                .map(item -> mapper.map(item, ItemDtoWithBookings.class))
                .collect(Collectors.toList());

        return getAllItemsWithBookings(items,page);
    }

    private List<ItemDtoWithBookings> getAllItemsWithBookings(List<ItemDtoWithBookings> list,Pageable page) {
        List<Booking> bookings = bookingsRepository
                .findAll(page)
                .stream()
                .collect(Collectors.toList());
        for (ItemDtoWithBookings item : list) {
            ResponseBookingDto lastBooking = bookings
                    .stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()) && booking.getStart().isBefore(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getEnd))
                    .map(booking -> bookingMapper.toResponseBookingDto(booking))
                    .findFirst()
                    .orElse(null);
            item.setLastBooking(lastBooking);

            ResponseBookingDto nextBooking = bookings
                    .stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()) && booking.getStart()
                            .isAfter(LocalDateTime.now()) && booking.getStatus().equals(Status.APPROVED))
                    .sorted(Comparator.comparing(Booking::getStart))
                    .map(booking -> bookingMapper.toResponseBookingDto(booking))
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

        return commentMapper.toCommentDto(comment);
    }

    private List<CommentDto> getAllComments(Long itemId) {
        return commentRepository
                .findCommentByItemId(itemId)
                .stream()
                .map(comment -> commentMapper.toCommentDto(comment))
                .collect(Collectors.toList());
    }

}


