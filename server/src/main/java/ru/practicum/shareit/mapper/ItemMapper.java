package ru.practicum.shareit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Item;


@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    ItemDto toItemDto(Item item);

    Item toItem(ItemDto itemDto);

    ItemDtoWithBookings toItemDtoWithBookings(Item item);

    Item toItem(ItemDtoWithBookings itemDtoWithBookings);


}
