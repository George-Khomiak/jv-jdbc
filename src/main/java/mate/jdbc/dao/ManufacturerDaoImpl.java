package mate.jdbc.dao;

import mate.jdbc.lib.Dao;
import mate.jdbc.lib.DataProcessingException;
import mate.jdbc.models.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class ManufacturerDaoImpl implements ManufacturerDao{
    @Override
    public Manufacturer create(Manufacturer manufacturer) {
        String insertManufacturerRequest = "INSERT INTO manufacturers(name, country) values(?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createManufacturerStatement
                     = connection.prepareStatement(insertManufacturerRequest, Statement.RETURN_GENERATED_KEYS)) {
            createManufacturerStatement.setString(1, manufacturer.getName());
            createManufacturerStatement.setString(2, manufacturer.getCountry());
            createManufacturerStatement.executeUpdate();
            ResultSet generatedKeys = createManufacturerStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                manufacturer.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t create manufacturer. " + manufacturer + " ", e);
        }
        return manufacturer;
    }

    @Override
    public Optional<Manufacturer> get(Long id) {
        String getManufacturerRequest = "SELECT * FROM manufacturers WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getStatement = connection.prepareStatement(getManufacturerRequest)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            Manufacturer manufacturer = null;
            if (resultSet.next()) {
                manufacturer = getManufacturer(resultSet);
            }
            return Optional.ofNullable(manufacturer);
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get manufacturer by id "
                    + id + " ", throwable);
        }
    }

    @Override
    public List<Manufacturer> getAll() {
        String getAllRequest = "SELECT * FROM manufacturers WHERE is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllStatement = connection.prepareStatement(getAllRequest)) {
            List<Manufacturer> manufacturers = new ArrayList<>();
            ResultSet resultSet = getAllStatement.executeQuery(getAllRequest);
            while (resultSet.next()) {
                manufacturers.add(getManufacturer(resultSet));
            }
            return manufacturers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can`t get manufacturers from table. ", throwable);
        }
    }

    @Override
    public Manufacturer update(Manufacturer manufacturer) {
        String updateManufacturersRequest = "UPDATE manufacturers SET name = ?, country = ? WHERE id = ?" +
                " AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement updateManufacturersStatement
                     = connection.prepareStatement(updateManufacturersRequest, Statement.RETURN_GENERATED_KEYS)) {
            updateManufacturersStatement.setString(1, manufacturer.getName());
            updateManufacturersStatement.setString(2, manufacturer.getCountry());
            updateManufacturersStatement.setLong(3, manufacturer.getId());
            updateManufacturersStatement.executeUpdate();
            ResultSet generatedKeys = updateManufacturersStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                manufacturer.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update a manufacturer" + manufacturer + " ", e);
        }
        return manufacturer;

    }

    @Override
    public boolean delete(Long id) {
        String deleteManufacturerRequest = "UPDATE manufacturers SET is_deleted = true where id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createFormatStatement
                     = connection.prepareStatement(deleteManufacturerRequest, Statement.RETURN_GENERATED_KEYS)) {
            createFormatStatement.setLong(1, id);
            createFormatStatement.executeUpdate();
            return createFormatStatement.executeUpdate() >= 1;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete manufacturer from db", e);
        }
    }

    private Manufacturer getManufacturer (ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(name, country);
        manufacturer.setId(id);
        return manufacturer;
    }
}
