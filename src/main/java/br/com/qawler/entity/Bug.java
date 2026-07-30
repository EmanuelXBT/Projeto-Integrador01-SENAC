package br.com.qawler.entity;

import br.com.qawler.enums.Severidade;
import br.com.qawler.enums.TipoBug;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bug")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teste_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Teste teste;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pagina_visitada_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private PaginaVisitada paginaVisitada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoBug tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severidade severidade;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    private Integer linha;

    private Integer coluna;

    @Column(name = "detectado_em", nullable = false)
    private LocalDateTime detectadoEm;

    @OneToMany(mappedBy = "bug", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Screenshot> screenshots = new ArrayList<>();
}
