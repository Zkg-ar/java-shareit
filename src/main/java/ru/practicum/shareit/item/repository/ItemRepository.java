package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item,Long> {
    @Query("select item from Item item "
            + "where lower(item.name) like lower(concat('%', ?1, '%')) "
            + "or lower(item.description) like lower(concat('%', ?1, '%')) and item.available = true ")
    List<Item> findItemByText(@Param("text")String text);

    @Query(nativeQuery = true,value = "SELECT items.name,items.description,items.is_available," +
            " bookings.start_date as lastBooking ,bookings.start_date as nextBooking FROM items LEFT JOIN bookings ON items.id = bookings.item_id" +
            " WHERE bookings.state = 'APPROVED' order by BOOKINGS.START_DATE LIMIT 1;")
    List<ItemDto> findAllItems(Long userId);
    List<Item> findAllByOwnerOrderById(User owner);
}
