package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories;";

        try(
            Connection connection = this.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            ){
            while (resultSet.next()){
                categories.add(mapRow(resultSet));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        // get all categories
        return categories;
    }

    @Override
    public Category getById(int id) {
        String query = "SELECT * FROM categories WHERE category_id=?;";
        try(
                Connection connection = this.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ){
            preparedStatement.setInt(1,id);
            try (
                    ResultSet resultSet = preparedStatement.executeQuery();
                    ){
                if (resultSet.next()){
                    return mapRow(resultSet);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Category create(Category category)
    {
        String sql = "INSERT INTO categories(category_id, name, description)"+
                " VALUES (?,?,?);";
        try (Connection connection = getConnection()){
        PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        statement.setInt(1, category.getCategoryId());
        statement.setString(2, category.getName());
        statement.setString(3,category.getDescription());

        int rowsAffected = statement.executeUpdate();

        if (rowsAffected > 0){
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()){
                int orderId = generatedKeys.getInt(1);
                return getById(orderId);
            }
        }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        // create a new category
        return null;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        String sql = "UPDATE categories"+
                "SET name = ?" +
                " ,description = ?" +
                "WHERE category_id = ?;";
        try(Connection connection = getConnection()){
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, category.getName());
            statement.setString(2,category.getDescription());
            statement.setInt(3, categoryId);

            statement.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        // update category
    }

    @Override
    public void delete(int categoryId) {
        String sql = "DELETE FROM categories" +
                "WHERE category_id = ?;";

        try (Connection connection = getConnection()){
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,categoryId);
            statement.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        // delete category
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
