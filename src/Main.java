import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class Main {
    static int currentIndex = 0;
    public static void main(String[] args) {

        ArrayList<Employee> employees = new ArrayList<>();

        final String url = "jdbc:mysql://localhost:3306/aulajava";
        final String username = "root";
        final String password = "123456";
        final String sql = "SELECT nome_func, sal_func, ds_cargo FROM tbfuncs INNER JOIN tbcargos ON tbfuncs.cod_cargo = tbcargos.cd_cargo WHERE nome_func LIKE ?";

        final JFrame frame = new JFrame("TP04 - LP2I4");
        frame.setSize(350, 225);
        frame.setLayout(new BorderLayout(0, 10));

        // SearchForm

        final JPanel formPanel = new JPanel();
        final BorderLayout formLayout = new BorderLayout();
        formPanel.setLayout(formLayout);

        final JPanel searchFieldPanel = new JPanel();
        final FlowLayout searchFieldLayout = new FlowLayout(FlowLayout.CENTER);
        searchFieldPanel.setLayout(searchFieldLayout);

        final JTextField searchTextField = new JTextField();
        searchTextField.setColumns(20);

        final JLabel nameLabel = new JLabel("Nome: ", SwingConstants.RIGHT);

        searchFieldPanel.add(nameLabel);
        searchFieldPanel.add(searchTextField);

        final JPanel searchButtonPanel = new JPanel();
        final FlowLayout searchButtonLayout = new FlowLayout(FlowLayout.CENTER);
        final JButton searchButton = new JButton("Pesquisar");
        searchButtonPanel.setLayout(searchButtonLayout);

        searchButtonPanel.add(searchButton);

        formPanel.add(searchFieldPanel, BorderLayout.NORTH);
        formPanel.add(searchButtonPanel, BorderLayout.SOUTH);

        frame.add(formPanel, BorderLayout.NORTH);

        // DisplayView

        final JPanel displayPanel = new JPanel();
        final GridLayout displayLayout = new GridLayout(4, 2);
        displayPanel.setLayout(displayLayout);

        final JTextField nameTextField = new JTextField();
        final JTextField wageTextField = new JTextField();
        final JTextField roleTextField = new JTextField();

        final JButton previousButton = new JButton("Anterior");
        final JButton nextButton = new JButton("Próximo");

        previousButton.setEnabled(false);
        nextButton.setEnabled(false);

        displayPanel.add(new JLabel("Nome: "));
        displayPanel.add(nameTextField);
        displayPanel.add(new JLabel("Salário: "));
        displayPanel.add(wageTextField);
        displayPanel.add(new JLabel("Cargo: "));
        displayPanel.add(roleTextField);
        displayPanel.add(previousButton);
        displayPanel.add(nextButton);

        frame.add(displayPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        searchButton.addActionListener(e -> {
            final String name = searchTextField.getText();

            nameTextField.setText("");
            wageTextField.setText("");
            roleTextField.setText("");

            employees.clear();

            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setString(1, "%" + name+ "%");

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String nome = resultSet.getString("nome_func");
                    int wage = resultSet.getInt("sal_func");
                    String role = resultSet.getString("ds_cargo");

                    final Employee employee = new Employee(nome, wage, role);

                    employees.add(employee);
                }

                if (employees.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Funcionário não encontrado.");

                    return;
                }

                currentIndex = 0;

                final Employee firstEmployee = employees.get(currentIndex);

                nameTextField.setText(firstEmployee.name);
                wageTextField.setText(String.valueOf(firstEmployee.wage));
                roleTextField.setText(firstEmployee.role);

                previousButton.setEnabled(false);

                nextButton.setEnabled(employees.size() > 1);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        previousButton.addActionListener(e -> {
            currentIndex--;

            final Employee employee = employees.get(currentIndex);

            nameTextField.setText(employee.name);
            wageTextField.setText(String.valueOf(employee.wage));
            roleTextField.setText(employee.role);

            nextButton.setEnabled(true);

            if (currentIndex == 0) previousButton.setEnabled(false);
        });

        nextButton.addActionListener(e -> {
            currentIndex++;

            final Employee employee = employees.get(currentIndex);

            nameTextField.setText(employee.name);
            wageTextField.setText(String.valueOf(employee.wage));
            roleTextField.setText(employee.role);

            previousButton.setEnabled(true);

            if (currentIndex == employees.size() - 1) nextButton.setEnabled(false);
        });
    }
}