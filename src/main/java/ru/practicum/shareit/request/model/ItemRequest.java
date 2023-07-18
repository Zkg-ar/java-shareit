package ru.practicum.shareit.request.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description", nullable = false, length = 200)
    private String description;
    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requester;
    @Column(name = "created_time", nullable = false)
    private LocalDateTime created;

}
