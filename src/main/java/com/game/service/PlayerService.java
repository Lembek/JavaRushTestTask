package com.game.service;

import com.game.entity.Player;
import com.game.entity.PlayerUtils;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Optional<Player> findOne(Long id) {
        return playerRepository.findById(id);
    }

    public void delete(Long id) {
        playerRepository.deleteById(id);
    }

    public boolean isExist(Long id) {
        return playerRepository.existsById(id);
    }

    public Player save(Player player) {
        setLevel(player);
        return playerRepository.save(player);
    }

    public boolean isValidAndNotNullParams(Player player) {
        return player.getName() != null && !player.getName().isEmpty() && player.getName().length() <= PlayerUtils.MAX_NAME_LENGTH
                && player.getTitle() != null && player.getTitle().length() <= PlayerUtils.MAX_TITLE_LENGTH
                && player.getRace() != null
                && player.getProfession() != null
                && player.getExperience() > 0 && player.getExperience() <= PlayerUtils.MAX_EXPERIENCE
                && player.getBirthday() != null && player.getBirthday().getTime() > 0
                && player.getBirthday().after(PlayerUtils.MIN_BIRTHDAY_DATE) && player.getBirthday().before(PlayerUtils.MAX_BIRTHDAY_DATE);
    }


    public boolean isValidId(String id) {
        try {
            return Long.parseLong(id) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void setLevel(Player player) {
        int level = (int) (Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100;
        player.setLevel(level);

        int untilNextLevel = 50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience();
        player.setUntilNextLevel(untilNextLevel);
    }

    public Map<String, String> checkParams(Map<String, String> params) {
         for (String key : PlayerUtils.params.keySet()) {
             if (!params.containsKey(key)) {
                 params.put(key, PlayerUtils.params.get(key));
             }
         }
         return params;
    }

    public PagedListHolder<Player> findWithFilters(Map<String, String> params) {
        List<Player> list =  playerRepository.findAllWithFilters(
                params.get("name"),
                params.get("title"),
                new Date(Long.parseLong(params.get("after"))),
                new Date(Long.parseLong(params.get("before"))),
                Integer.parseInt(params.get("minExperience")),
                Integer.parseInt(params.get("maxExperience")),
                Integer.parseInt(params.get("minLevel")),
                Integer.parseInt(params.get("maxLevel")));

        if (!params.get("banned").isEmpty())
            list = filterByBanned(list,Boolean.valueOf(params.get("banned")));

        if (!params.get("race").isEmpty())
            list = filterByRace(list,Race.valueOf(params.get("race")));

        if (!params.get("profession").isEmpty())
            list = filterByProfession(list,Profession.valueOf(params.get("profession")));

        sortByOrder(list,params.get("order").toLowerCase(Locale.ROOT));

        PagedListHolder<Player> pagedListHolder = new PagedListHolder<>(list);
        pagedListHolder.setPage(Integer.parseInt(params.get("pageNumber")));
        pagedListHolder.setPageSize(Integer.parseInt(params.get("pageSize")));

        return pagedListHolder;
    }

    /* Та самая дополнительная фильтрация по race, profession и banned */

    public List<Player> filterByRace(List<Player> list, Race race) {
        return list.stream()
                .filter(player -> player.getRace() == race)
                .collect(Collectors.toList());
    }

    public List<Player> filterByProfession(List<Player> list, Profession profession) {
        return list.stream()
                .filter(player -> player.getProfession() == profession)
                .collect(Collectors.toList());
    }

    public List<Player> filterByBanned(List<Player> list, Boolean banned) {
        return list.stream()
                .filter(player -> player.getBanned() == banned)
                .collect(Collectors.toList());
    }

    /* Та самая ручная сортировка */

    public void sortByOrder(List<Player> list, String order) {
        list.sort((o1, o2) -> {
            switch (order) {
                case "name":
                    return o1.getName().compareTo(o2.getName());
                case "birthday":
                    return (int) (o1.getBirthday().getTime()*1.0/1000 - o2.getBirthday().getTime()*1.0/1000);
                case "experience":
                    return o1.getExperience() - o2.getExperience();
                case "level":
                    return o1.getLevel() - o2.getLevel();
                default:
                    return (int) (o1.getId() - o2.getId());
            }
        });
    }

    /* Очень не красивый метод, признаю, но ничего лучше в голову не идёт.
    * Добрый человек, если ты знаешь, как превратить "это" в конфетку, пожалуйста,
    * расскажи рецепт) */

    public Player updateAndSave(Player oldPlayer, Player newPlayer) {
        if (newPlayer.getName() != null) {
            if (!newPlayer.getName().isEmpty() && newPlayer.getName().length() <= PlayerUtils.MAX_NAME_LENGTH)
                oldPlayer.setName(newPlayer.getName());
            else
                throw new IllegalArgumentException();
        }

        if (newPlayer.getTitle() != null) {
            if (newPlayer.getTitle().length() <= PlayerUtils.MAX_TITLE_LENGTH)
                oldPlayer.setTitle(newPlayer.getTitle());
            else
                throw new IllegalArgumentException();
        }

        if (newPlayer.getRace() != null)
            oldPlayer.setRace(newPlayer.getRace());

        if (newPlayer.getProfession() != null)
            oldPlayer.setProfession(newPlayer.getProfession());

        if (newPlayer.getBirthday() != null) {
            if (newPlayer.getBirthday().getTime() > 0
                    && newPlayer.getBirthday().after(PlayerUtils.MIN_BIRTHDAY_DATE) && newPlayer.getBirthday().before(PlayerUtils.MAX_BIRTHDAY_DATE))
                oldPlayer.setBirthday(newPlayer.getBirthday());
            else
                throw new IllegalArgumentException();
        }

        if (newPlayer.getBanned() != null)
            oldPlayer.setBanned(newPlayer.getBanned());

        if (newPlayer.getExperience() != null) {
            if (newPlayer.getExperience() > 0 && newPlayer.getExperience() <= PlayerUtils.MAX_EXPERIENCE) {
                oldPlayer.setExperience(newPlayer.getExperience());
                setLevel(oldPlayer);
            } else
                throw new IllegalArgumentException();
        }

        return playerRepository.save(oldPlayer);
    }


}
