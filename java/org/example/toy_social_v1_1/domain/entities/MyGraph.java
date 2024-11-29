package org.example.toy_social_v1_1.domain.entities;

import java.util.*;
import java.util.function.Predicate;

public class MyGraph<ID extends Comparable<ID>, E extends Entity<ID>> {
    private HashMap<E, List<E>> G;
    private Iterable<E> all;
    private int compCount;

    private int maxCompSize;
    private E maxCompHead;

    public MyGraph(Iterable<E> all, List<List<E>> links) {
        G = new HashMap<>();
        refresh(all, links);
    }

    public void refresh(Iterable<E> all, List<List<E>> links) {
        this.all = all;
        links.forEach(link -> {
            Predicate<E> added = x -> G.containsKey(x);
            if(!added.test(link.getFirst()))
                G.put(link.getFirst(), new ArrayList<>());
            G.get(link.getFirst()).add(link.get(1));
        });
    }

    public int getCompCount() {
        countComps();
        return compCount;
    }

    public void setAll(Iterable<E> newAll) {
        all = newAll;
    }

    public List<E> getNeighbours(E e){
        return G.get(e);
    }

    private void dfs(E node, List<E> seen) {
        seen.add(node);

        G.get(node).forEach(v -> {
            if(!seen.contains(v))
                dfs(v, seen);
        });
    }

    public void countComps(){
        compCount = 0;
        List<E> seen = new ArrayList<>();
        all.forEach(elem -> {
            if(!seen.contains(elem)) {
                if(G.containsKey(elem)) {
                    int prev = seen.size();
                    dfs(elem, seen);
                    int post = seen.size();
                    int size = post - prev;
                    if(size > maxCompSize) {
                        maxCompSize = size;
                        maxCompHead = elem;
                    }
                }
                compCount ++;
            }
        });
    }

    public List<E> maxComp(){
        countComps();
        List<E> res = new ArrayList<>();
        dfs(maxCompHead, res);
        return res;
    }

    public void unlink(E v1, E v2){
        if(!G.containsKey(v1))
            throw new IllegalArgumentException("No such vertex");
        if(!G.get(v1).contains(v2))
            throw new IllegalArgumentException("No link existent between the 2 vertexes");

        G.get(v1).remove(v2);
    }

    public boolean link(E v1, E v2){
        if(!G.containsKey(v1))
            G.put(v1, new ArrayList<>());

        if(G.get(v1).contains(v2))
            return false;

        G.get(v1).add(v2);
        return true;
    }

    public void unlinkEntity(E entity) {
        all.forEach(e -> {
            if(!e.equals(entity)) {
                try{
                    unlink(e, entity);
                } catch (Exception ex) {
                    // continue
                }
            }
        });
    }
}
