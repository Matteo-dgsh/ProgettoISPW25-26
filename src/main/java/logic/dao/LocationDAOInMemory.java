package logic.dao;

import logic.utils.InMemoryDataStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione di ILocationDAO usata in modalita' Demo: province e citta'
 * sono dati di riferimento statici tenuti in InMemoryDataStore, nessuna query SQL.
 */
public class LocationDAOInMemory implements ILocationDAO {

    @Override
    public List<String> getProvincesList() {
        return new ArrayList<>(InMemoryDataStore.citiesByProvince.keySet());
    }

    @Override
    public List<String> getCitiesList(String selectedProvince) {
        return new ArrayList<>(InMemoryDataStore.citiesByProvince.getOrDefault(selectedProvince, new ArrayList<>()));
    }
}
