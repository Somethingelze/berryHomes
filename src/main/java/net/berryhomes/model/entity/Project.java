package net.berryhomes.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "projects", schema = "berryhomes")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "city_zip", nullable = false, length = 100)
    private String cityZip;

    @Column(name = "purchase_price", length = 50)
    private String purchasePrice;

    @Column(name = "monthly_rent", length = 50)
    private String monthlyRent;

    @Column(name = "renovation_budget", length = 50)
    private String renovationBudget;

    @Column(name = "est_noi_annual", length = 50)
    private String estNoiAnnual;

    @Column(name = "total_investment", length = 50)
    private String totalInvestment;

    @Column(name = "cash_on_cash_return", length = 50)
    private String cashOnCashReturn;

    @Column(name = "est_payback", length = 50)
    private String estPayback;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProjectImage> projectImages = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProjectDocument> projectDocuments = new ArrayList<>();
}
