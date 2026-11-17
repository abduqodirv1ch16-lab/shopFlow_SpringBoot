package uz.pdp.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 150)
    private String street;

    @Column(name = "house_number", nullable = false, length = 50)
    private String houseNumber;

    @Column(name = "additional_info", length = 255)
    private String additionalInfo;
}
