package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select item from Item item "
            + "where lower(item.name) like lower(concat('%', ?1, '%')) "
            + "or lower(item.description) like lower(concat('%', ?1, '%')) and item.available = true ")
    List<Item> findItemByText(@Param("text") String text,Pageable page);


    List<Item> findAllByOwnerOrderById(User owner, Pageable page);

    List<Item> findByRequestId(Long requestId);
}
