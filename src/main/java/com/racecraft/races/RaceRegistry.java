package com.racecraft.races;

import com.racecraft.races.impl.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RaceRegistry {

    private final Map<String, RaceMastery> races = new LinkedHashMap<>();

    public void register(RaceMastery race) {
        races.put(race.getId().toLowerCase(), race);
    }

    public void registerAllDefaults() {
        // 16 Chủng Tộc có thể quay ra từ Trứng Huyết Mạch
        register(new EndermanRace());
        register(new CaRace());
        register(new NguoiRace());
        register(new IronRace());
        register(new XuongDenRace());
        register(new TienRungRace());
        register(new AcQuyRace());
        register(new ThienSuRace());
        register(new NguoiLunRace());
        register(new MaCaRongRace());
        register(new RongRace());
        register(new HoRace());
        register(new PhuongHoangRace());
        register(new ChimRace());
        register(new KyLanRace());
        register(new VikingRace());

        // Tộc đặc biệt khi từ bỏ huyết mạch 2 lần
        register(new PheTocRace());
    }

    public RaceMastery get(String id) {
        if (id == null) return null;
        return races.get(id.toLowerCase());
    }

    public Collection<RaceMastery> getAll() {
        return races.values();
    }

    public List<RaceMastery> getRollableRaces() {
        List<RaceMastery> list = new ArrayList<>();
        for (RaceMastery r : races.values()) {
            if (r.isRollable()) {
                list.add(r);
            }
        }
        return list;
    }
}
