package ru.kozlov.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "cats")
public class Cat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalDate birthDate;
    private String breed;
    private Colors color;
    @ManyToOne
    private CatOwner owner;
    @ManyToMany
    @JoinTable(
            name = "friends",
            joinColumns = @JoinColumn(name = "cat_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id", referencedColumnName = "id"))
    private Set<Cat> friends = new HashSet<>();

    public Cat(String name, LocalDate birthDate, String breed, Colors color) {
        this.name = name;
        this.birthDate = birthDate;
        this.breed = breed;
        this.color = color;
    }

    public Cat(Long id, String name, LocalDate birthDate, String breed, Colors color) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.breed = breed;
        this.color = color;
    }

    public void addFriend(Cat friend) {
        friends.add(friend);
    }
}
