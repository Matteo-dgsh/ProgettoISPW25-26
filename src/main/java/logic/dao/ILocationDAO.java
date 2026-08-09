package logic.dao;

import java.util.List;

public interface ILocationDAO {
    List<String> getProvincesList();

    List<String> getCitiesList(String selectedProvince);
}
