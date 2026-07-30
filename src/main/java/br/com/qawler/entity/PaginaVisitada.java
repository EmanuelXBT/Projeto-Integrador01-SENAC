package br.com.qawler.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pagina_visitada")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginaVisitada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teste_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Teste teste;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "http_status", nullable = false)
    private Integer httpStatus;

    @Column(name = "tempo_carregamento_ms", nullable = false)
    private Integer tempoCarregamentoMs;

    @Column(length = 255)
    private String titulo;

    @Column(name = "visitada_em", nullable = false)
    private LocalDateTime visitadaEm;

    @OneToMany(mappedBy = "paginaVisitada", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Bug> bugs = new ArrayList<>();

    @OneToMany(mappedBy = "paginaVisitada", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Screenshot> screenshots = new ArrayList<>();
}
