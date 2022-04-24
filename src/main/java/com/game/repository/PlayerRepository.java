package com.game.repository;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface PlayerRepository extends JpaRepository<Player, Long> {

    /* Не знаю, как поместить в запрос фильтрацию по race, profession и banned, из-за чего
    * пришлось в сервисе проводить дополнительную фильтрацию с помощью stream`ов
    * и после вручную реализовывать пагинацию.
    * Также не смог реализовать сортировку ни на уровне репозитория, ни на уровне ручной пагинации,
    * опять-таки сделал её вручную.
    * Дорогой друг, если ты это читаешь и знаешь, как решить данные проблемы, поделись своей мудрстью,
    * буду очень благодарен!) */

    @Query("SELECT p FROM Player p WHERE p.name LIKE %:name% AND p.title LIKE %:title% AND p.birthday BETWEEN :after AND :before AND p.experience BETWEEN :minExperience AND :maxExperience AND p.level BETWEEN :minLevel AND :maxLevel")
    List<Player> findAllWithFilters(@Param("name") String name,
                                    @Param("title") String title,
                                    @Param("after") Date after,
                                    @Param("before") Date before,
                                    @Param("minExperience") Integer minExperience,
                                    @Param("maxExperience") Integer maxExperience,
                                    @Param("minLevel") Integer minLevel,
                                    @Param("maxLevel") Integer maxLevel);

}
