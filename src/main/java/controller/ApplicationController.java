
package in.bawvpl.Authify.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1.0/application")
public class ApplicationController {

    private Map<Long, Map<String,Object>> db = new HashMap<>();
    private long id=1;

    @PostMapping
    public Map<String,Object> create(@RequestBody Map<String,Object> body){
        body.put("id",id++);
        db.put((Long)body.get("id"), body);
        return body;
    }

    @GetMapping
    public Collection<Map<String,Object>> all(){
        return db.values();
    }

    @GetMapping("/{id}")
    public Map<String,Object> one(@PathVariable Long id){
        return db.get(id);
    }

    @PutMapping("/{id}")
    public Map<String,Object> update(@PathVariable Long id,@RequestBody Map<String,Object> body){
        body.put("id",id);
        db.put(id,body);
        return body;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id){
        db.remove(id);
        return "deleted";
    }
}
