package br.com.qawler.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "screenshot")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Screenshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bug_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Bug bug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pagina_visitada_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private PaginaVisitada paginaVisitada;

    @Column(name = "caminho_arquivo", nullable = false, length = 500)
    private String caminhoArquivo;

    @Column(nullable = false)
    private Integer largura;

    @Column(nullable = false)
    private Integer altura;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
    }
}
