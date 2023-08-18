package ru.practicum.ewm.event.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    Long id;

    @Column(nullable = false)
    Float lat;

    @Column(nullable = false)
    Float lon;
}
