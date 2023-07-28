package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequester_IdOrderByCreatedDesc(Long userId);

    @Query("SELECT request from ItemRequest as request WHERE request.requester.id <> ?1")
    Page<ItemRequest> findItemRequestByRequester_IdNot(Long userId,Pageable page);

}
