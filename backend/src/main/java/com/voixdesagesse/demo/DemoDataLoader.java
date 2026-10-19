package com.voixdesagesse.demo;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Component;

import com.voixdesagesse.dto.AccountType;
import com.voixdesagesse.dto.CategoryType;
import com.voixdesagesse.dto.HistoireDTO;
import com.voixdesagesse.dto.SagesseDTO;
import com.voixdesagesse.entity.Article;
import com.voixdesagesse.entity.User;
import com.voixdesagesse.exception.VoixDeSagesseException;
import com.voixdesagesse.repository.ArticleRepository;
import com.voixdesagesse.repository.UserRepository;
import com.voixdesagesse.service.ArticleService;
import com.voixdesagesse.service.CommentService;
import com.voixdesagesse.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Fills an empty database with demo accounts, articles and reactions, so the app can be tried right away
 * (docker compose, online demo). Enabled with DEMO_DATA=true; does nothing once the database has users.
 * Everything goes through the services, so counters and notifications are the same as for real use.
 */
@Component
@ConditionalOnBooleanProperty("app.demo-data.enabled")
@RequiredArgsConstructor
@Slf4j
public class DemoDataLoader implements ApplicationRunner {

    public static final String DEMO_EMAIL = "demo@voixdesagesse.local";
    public static final String ADMIN_EMAIL = "admin@voixdesagesse.local";

    @Value("${app.demo-data.user-password}")
    private String userPassword;

    // Empty: no admin account is created.
    @Value("${app.demo-data.admin-password:}")
    private String adminPassword;

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final UserService userService;
    private final ArticleService articleService;
    private final CommentService commentService;

    @Override
    public void run(ApplicationArguments args) throws VoixDeSagesseException {
        if (userRepository.count() > 0) {
            log.info("Demo data skipped: the database already has users");
            return;
        }

        User demo = account("Démo", "Visiteur", DEMO_EMAIL);
        User karim = account("Benali", "Karim", "karim@voixdesagesse.local");
        User amina = account("El Idrissi", "Amina", "amina@voixdesagesse.local");
        User youssef = account("Alaoui", "Youssef", "youssef@voixdesagesse.local");
        if (!adminPassword.isBlank())
            userService.createAccount("Admin", "Voix", ADMIN_EMAIL, adminPassword, AccountType.ADMIN);

        Article patience = wisdom(karim, 13, "La patience est un arbre dont la racine est amère, mais dont les fruits sont doux.",
                "Proverbe persan", CategoryType.sagesse_populaire, "patience");
        Article montagne = wisdom(amina, 12, "Celui qui déplace une montagne commence par déplacer de petites pierres.",
                "Confucius", CategoryType.citations_celebres, "persévérance");
        Article ensemble = wisdom(youssef, 10, "Si tu veux aller vite, marche seul. Si tu veux aller loin, marchons ensemble.",
                "Proverbe africain", CategoryType.sagesse_populaire, "entraide");
        Article coeur = wisdom(demo, 8, "On ne voit bien qu'avec le cœur. L'essentiel est invisible pour les yeux.",
                "Antoine de Saint-Exupéry, Le Petit Prince", CategoryType.citations_celebres, "amitié");
        Article ecoute = wisdom(karim, 6, "Prendre le temps d'écouter quelqu'un, c'est lui offrir un cadeau qui ne coûte rien et qui vaut beaucoup.",
                null, CategoryType.vie_quotidienne, "écoute");
        Article aide = story(youssef, 5, "Le jour où j'ai demandé de l'aide",
                "Pendant des mois, j'ai voulu tout porter seul : le travail, les soucis de la famille, les doutes. "
                        + "Un soir, épuisé, j'ai appelé un ami d'enfance que je n'avais pas vu depuis longtemps. "
                        + "Il est venu le lendemain, sans poser de questions, et nous avons parlé jusqu'à tard. "
                        + "Rien n'était réglé, mais pour la première fois je n'étais plus seul face à tout cela.",
                "Demander de l'aide est une force, pas une faiblesse.", CategoryType.emouvante, "entraide", "amitié");
        Article marathon = story(amina, 3, "Mon premier marathon, en marchant",
                "Je m'étais inscrite à un marathon sur un coup de tête, sans vraiment m'entraîner. Au trentième kilomètre, "
                        + "mes jambes ont dit stop. J'ai fini en marchant, presque dernière, sous les applaudissements des bénévoles "
                        + "qui rangeaient déjà les barrières. Je n'ai jamais été aussi fière d'un mauvais temps.",
                "Finir, c'est déjà gagner.", CategoryType.inspirante, "défi", "sport");
        Article lettre = story(demo, 2, "La lettre de ma grand-mère",
                "En vidant la maison de ma grand-mère, j'ai trouvé une lettre qu'elle m'avait écrite pour mes dix-huit ans "
                        + "et qu'elle n'avait jamais envoyée. Elle y parlait de courage, de gentillesse et du thé qu'on buvait "
                        + "ensemble le dimanche. Je la relis chaque fois que je doute.",
                "Les mots écrits restent quand les voix se taisent.", CategoryType.emouvante, "famille");
        wisdom(youssef, 1, "Le meilleur moment pour planter un arbre, c'était il y a vingt ans. Le deuxième meilleur moment, c'est maintenant.",
                "Proverbe chinois", CategoryType.inspiration, "action");

        // Reactions between the authors; the demo account receives some, so its notifications are not empty.
        follow(demo, amina);
        follow(karim, demo);
        follow(amina, demo);
        follow(youssef, karim);
        like(karim, coeur, lettre, montagne, ensemble);
        like(amina, coeur, patience, aide);
        like(youssef, lettre, montagne, patience);
        like(demo, patience, montagne, marathon);
        comment(karim, coeur, "Une phrase que je relis souvent, merci de l'avoir partagée.");
        comment(amina, lettre, "Très touchant, ça m'a rappelé ma propre grand-mère.");
        comment(demo, aide, "Merci pour ce partage, il faut du courage pour raconter ça.");
        comment(youssef, patience, "Tellement vrai, surtout quand on attend des résultats.");
        comment(karim, marathon, "Bravo ! L'important c'est d'avoir franchi la ligne.");
        save(youssef, coeur);
        save(demo, ensemble, ecoute);

        log.info("Demo data loaded: sign in with {} (admin account: {})", DEMO_EMAIL, adminPassword.isBlank() ? "none" : ADMIN_EMAIL);
    }

    private User account(String nom, String prenom, String email) throws VoixDeSagesseException {
        return userService.createAccount(nom, prenom, email, userPassword, AccountType.USER);
    }

    // `daysAgo` spreads the publication dates, so the feed does not show everything "à l'instant".
    private Article wisdom(User author, int daysAgo, String content, String source, CategoryType category, String... tags)
            throws VoixDeSagesseException {
        SagesseDTO dto = new SagesseDTO(content, source, category, Set.of(tags), author.getId());
        return backdate(articleService.createSagesseArticle(dto), daysAgo);
    }

    private Article story(User author, int daysAgo, String title, String content, String lesson, CategoryType category,
            String... tags) throws VoixDeSagesseException {
        HistoireDTO dto = new HistoireDTO(title, content, category, Set.of(tags), lesson, author.getId());
        return backdate(articleService.createHistoireArticle(dto), daysAgo);
    }

    private Article backdate(Article article, int daysAgo) {
        article.setDatePublication(LocalDateTime.now().minusDays(daysAgo).minusHours(daysAgo * 3L % 11));
        return articleRepository.save(article);
    }

    private void follow(User follower, User followed) throws VoixDeSagesseException {
        userService.followUser(follower.getId(), followed.getId());
    }

    private void like(User user, Article... articles) throws VoixDeSagesseException {
        for (Article article : articles)
            articleService.likeArticle(article.getId(), user.getId());
    }

    private void comment(User user, Article article, String content) throws VoixDeSagesseException {
        commentService.addComment(article.getId(), user.getId(), content);
    }

    private void save(User user, Article... articles) throws VoixDeSagesseException {
        for (Article article : articles)
            userService.saveArticle(user.getId(), article.getId());
    }
}
