package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rest/players")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }


    /* Не до конца уверен, что использование @RequestParam Map<String, String> params в этом месте -
    * лучший вариант. С радостью бы узнал более красивый вариант!! */

    @GetMapping
    public ResponseEntity<List<Player>> getAll(@RequestParam Map<String, String> params) {
        Map<String, String> newParams = playerService.checkParams(params);
        return new ResponseEntity<>(playerService.findWithFilters(newParams).getPageList(),HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getPlayerCount(@RequestParam Map<String, String> params) {
        Map<String, String> newParams = playerService.checkParams(params);
        return new ResponseEntity<>(playerService.findWithFilters(newParams).getSource().size(), HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (!playerService.isValidId(id))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (playerService.isExist(Long.parseLong(id))) {
            playerService.delete(Long.parseLong(id));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping()
    public ResponseEntity<Player> create(@RequestBody Player player) {
        return playerService.isValidAndNotNullParams(player)
                ? new ResponseEntity<>(playerService.save(player), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Player> update(@PathVariable String id, @RequestBody Player newPlayer) {
        if (!playerService.isValidId(id))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!playerService.isExist(Long.parseLong(id)))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Player oldPlayer = playerService.findOne(Long.parseLong(id)).get();
        try {
            return new ResponseEntity<>(playerService.updateAndSave(oldPlayer,newPlayer), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> findOne(@PathVariable String id) {
        if (!playerService.isValidId(id))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return playerService.findOne(Long.parseLong(id)).isPresent()
                ? new ResponseEntity<>(playerService.findOne(Long.parseLong(id)).get(), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
