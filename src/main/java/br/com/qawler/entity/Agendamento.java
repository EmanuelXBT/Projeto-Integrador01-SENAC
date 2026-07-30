package br.com.qawler.entity;

import br.com.qawler.enums.Frequencia;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agendamento")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sistema_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Sistema sistema;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Frequencia frequencia;

    @Column(nullable = false)
    private LocalTime horario;

    @Column(name = "dia_semana")
    private Integer diaSemana;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @Column(name = "proxima_execucao", nullable = false)
    private LocalDateTime proximaExecucao;

    @Column(name = "ultima_execucao")
    private LocalDateTime ultimaExecucao;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @OneToMany(mappedBy = "agendamento")
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Teste> testes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
    }
}
