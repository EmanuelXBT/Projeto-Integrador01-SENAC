package br.com.qawler.entity;

import br.com.qawler.enums.Ambiente;
import br.com.qawler.enums.ModoCrawler;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sistema")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Usuario usuario;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "url_base", nullable = false, length = 500)
    private String urlBase;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Ambiente ambiente;

    @Column(name = "credenciais_login", length = 255)
    private String credenciaisLogin;

    @Column(name = "credenciais_senha", length = 255)
    private String credenciaisSenha;

    @Column(name = "dominios_autorizados", nullable = false, columnDefinition = "JSON")
    private String dominiosAutorizados;

    @Column(name = "profundidade_crawl", nullable = false)
    @Builder.Default
    private Integer profundidadeCrawl = 2;

    @Enumerated(EnumType.STRING)
    @Column(name = "modo_crawler", nullable = false)
    @Builder.Default
    private ModoCrawler modoCrawler = ModoCrawler.FULL;

    @Column(name = "autorizado_producao", nullable = false)
    @Builder.Default
    private Boolean autorizadoProducao = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "sistema", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Teste> testes = new ArrayList<>();

    @OneToMany(mappedBy = "sistema", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Agendamento> agendamentos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
}
