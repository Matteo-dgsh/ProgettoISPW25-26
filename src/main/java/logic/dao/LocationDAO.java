package logic.dao;

import logic.utils.SingletonDBSession;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static logic.view.EssentialGUI.logger;

public class LocationDAO implements ILocationDAO {
    public List<String> getProvincesList(){
        List<String> provincesList = new ArrayList<>();
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT name FROM provinces")) {
            buildProvincesArray(statement, provincesList);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred during the fetch provinces from DB");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return provincesList;
    }

    private void buildProvincesArray(PreparedStatement statement, List<String>provs) {
        try (ResultSet rs = statement.executeQuery()){
            while (rs.next()){
                provs.add(rs.getString(1));
            }
        } catch (SQLException e){
            logger.log(Level.SEVERE, "SQLException occurred while executing query to DB to fetch provinces");
        }
    }

    public List<String> getCitiesList(String selectedProvince){
        List<String> citiesList = new ArrayList<>();
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT name FROM cities WHERE province = ?")) {
            statement.setString(1, selectedProvince);
            buildCitiesArray(statement,citiesList);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred during the fetch provinces from DB");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return citiesList;
    }

    private void buildCitiesArray(PreparedStatement statement, List<String> cities) {
        try (ResultSet rs = statement.executeQuery()){
            while (rs.next()){
                cities.add(rs.getString(1));
            }
        } catch (SQLException e){
            logger.log(Level.SEVERE, "SQLException occurred while executing query to DB to fetch provinces");
        }
    }
}
