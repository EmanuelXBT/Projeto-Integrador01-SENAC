package br.com.qawler.entity;

import br.com.qawler.enums.StatusTeste;
import br.com.qawler.enums.TipoDisparo;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teste")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sistema_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Sistema sistema;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusTeste status = StatusTeste.QUEUED;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_disparo", nullable = false)
    private TipoDisparo tipoDisparo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agendamento_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Agendamento agendamento;

    @Column(name = "paginas_visitadas")
    @Builder.Default
    private Integer paginasVisitadas = 0;

    @Column(name = "total_bugs")
    @Builder.Default
    private Integer totalBugs = 0;

    @Column(name = "duracao_ms")
    private Integer duracaoMs;

    @Column(name = "erro_mensagem", columnDefinition = "TEXT")
    private String erroMensagem;

    @Column(name = "inicio_em")
    private LocalDateTime inicioEm;

    @Column(name = "fim_em")
    private LocalDateTime fimEm;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @OneToMany(mappedBy = "teste", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<PaginaVisitada> paginasVisitadasList = new ArrayList<>();

    @OneToMany(mappedBy = "teste", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Bug> bugs = new ArrayList<>();

    @OneToMany(mappedBy = "teste", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Notificacao> notificacoes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
    }
}
