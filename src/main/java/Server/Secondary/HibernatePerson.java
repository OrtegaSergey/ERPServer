package Server.Secondary;

import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "clients")
public class HibernatePerson implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String address;

    @Column
    private Timestamp agreed;

    @Column
    private String name;

    @Column
    private String phone;

    @Column(name = "mail")
    private String email;

    @Column(name = "creation_date")
    private Date created;

    @Column
    private Date archived;

    @Column
    private String createdBy;

    @Column
    private int branch;

    @Column
    private boolean alert;
}
