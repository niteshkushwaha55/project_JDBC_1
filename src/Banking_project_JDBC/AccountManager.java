package Banking_project_JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {
    private Connection connection;
    private Scanner scanner;
    public AccountManager(Connection connection,Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void credit_money(long account_number) throws SQLException{
        scanner.nextLine();
        System.out.print("Enter Amount: ");
        double amount=scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security pin: ");
        String security_pin= scanner.nextLine();
        try {
            connection.setAutoCommit(false);

            if(account_number!=0) {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM accounts WHERE account_number = ? AND security_pin = ?");
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet=preparedStatement.executeQuery();
                if(resultSet.next()){
                        String credit_balance = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
                        PreparedStatement preparedStatement1= connection.prepareStatement(credit_balance);
                        preparedStatement1.setDouble(1,amount);
                        preparedStatement1.setLong(2,account_number);
                        int affectedRows=preparedStatement1.executeUpdate();
                        if(affectedRows > 0){
                            System.out.println("Rs." + amount + "credited successfully" );
                            connection.commit();
                            connection.setAutoCommit(true);
                            return;
                        }else {
                            System.out.println("Transaction Failed!");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                }else System.out.println("Invaild pin!");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public void debit_money(long account_number) throws SQLException{
        scanner.nextLine();
        System.out.print("Enter Amount: ");
        double amount=scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security pin: ");
        String security_pin= scanner.nextLine();
        try {
            connection.setAutoCommit(false);
            if(account_number!=0) {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM accounts WHERE account_number = ? AND security_pin = ?");
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet=preparedStatement.executeQuery();
                if(resultSet.next()){
                    double current_balance =resultSet.getDouble("balance");
                    if(amount<=current_balance){
                        String debit_query = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
                        PreparedStatement preparedStatement1= connection.prepareStatement(debit_query);
                        preparedStatement1.setDouble(1,amount);
                        preparedStatement1.setLong(2,account_number);
                        int affectedRows=preparedStatement1.executeUpdate();
                        if(affectedRows > 0){
                            System.out.println("Rs." + amount + "debited successfully" );
                            connection.commit();
                            connection.setAutoCommit(true);
                            return;
                        }else {
                            System.out.println("Transaction Failed!");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    }else System.out.println("Insufficient Balance!");
                }else System.out.println("Invaild pin!");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }
    public  void transfer_money(long sender_account_number) throws SQLException{
        scanner.nextLine();
        System.out.print("Enter Receiver Account Number: ");
        long receiver_acc_number=scanner.nextLong();
        System.out.print("Enter Amount: ");
        double amount=scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security pin");
        String security_pin=scanner.nextLine();
        String sql="SELECT * FROM accounts WHERE account_number = ? AND security_pin = ?";
        try {
            connection.setAutoCommit(false);
            if(sender_account_number !=0 && receiver_acc_number != 0){
                PreparedStatement preparedStatement= connection.prepareStatement(sql);
                preparedStatement.setLong(1,sender_account_number);
                preparedStatement.setString(2,security_pin);
                ResultSet resultSet=preparedStatement.executeQuery();
                if(resultSet.next()){
                    double current_balance = resultSet.getDouble("balance");
                    if(amount <= current_balance){
                        String debit_query="UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
                        String credit_query="UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
                        PreparedStatement creditPreparedStatement = connection.prepareStatement(credit_query);
                        PreparedStatement debitPreparedStatement = connection.prepareStatement(debit_query);
                        creditPreparedStatement.setDouble(1,amount);
                        creditPreparedStatement.setLong(2,receiver_acc_number);
                        debitPreparedStatement.setDouble(1,amount);
                        debitPreparedStatement.setLong(2,sender_account_number);
                        int affectedRows1=debitPreparedStatement.executeUpdate();
                        int affectedRows2 =creditPreparedStatement.executeUpdate();
                        if(affectedRows1 > 0 && affectedRows2 > 0){
                            System.out.println("Transaction Successfully!");
                            System.out.println("Rs." + amount + "Transferred successfully");
                            connection.commit();
                            connection.setAutoCommit(true);
                        }else {
                            System.out.println("Transaction failed!");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    }else System.out.println("Insufficient Balance!");
                }else System.out.println("Invalid Securit pin");
            }else System.out.println("Invalid Account number");
        }catch (SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }
    public  void getBalance(long account_number){
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();
        String sql="SELECT balance FROM accounts WHERE account_number = ? AND security_pin = ?";
        try {
            PreparedStatement preparedStatement= connection.prepareStatement(sql);
            preparedStatement.setLong(1,account_number);
            preparedStatement.setString(2,security_pin);
            ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet.next()) {
                double balance= resultSet.getDouble("balance");
                System.out.println("Balance: " + balance);
            }else
                System.out.println("Invaild Pin");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
