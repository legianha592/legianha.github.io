package org.example.RestAPI.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "wallet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    @Id @GeneratedValue
    private long id;
    private String wallet_name;
    private LocalDateTime created_date;
    @PrePersist
    public void prePersist(){
        created_date = LocalDateTime.now();
        modified_date = LocalDateTime.now();
    }
    private LocalDateTime modified_date;
    @PreUpdate
    public void preUpdate(){
        modified_date = LocalDateTime.now();
    }
    private double total_amount;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;
    public void setUser(User user){
        this.user = user;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "wallet",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Record> listRecord = new ArrayList<>();
    public void addRecord(Record record){
        record.setWallet(this);
    }
}
