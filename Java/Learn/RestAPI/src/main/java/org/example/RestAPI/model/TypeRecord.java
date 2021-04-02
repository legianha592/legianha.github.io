package org.example.RestAPI.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "type_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String typeRecord_name;
    private String image_url;
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "typeRecord",
            cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonManagedReference
    private List<Record> listRecord = new ArrayList<>();
    public void addRecord(Record record){
        record.setTypeRecord(this);
    }

    @ManyToMany(mappedBy = "setTypeRecord")
    @JsonBackReference
    private List<Wallet> listWallet = new ArrayList<>();
    public void addWallet(Wallet wallet){
        listWallet.add(wallet);
    }
    public void deleteWallet(Wallet wallet){
        listWallet.remove(wallet);
    }
}
