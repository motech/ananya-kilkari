package org.motechproject.ananya.kilkari.smoke.service;

import org.apache.commons.dbcp.BasicDataSource;
import org.motechproject.ananya.kilkari.smoke.domain.report.SubscriptionStatusMeasure;
import org.motechproject.ananya.kilkari.smoke.utils.TimedRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReportService {

    private BasicDataSource dataSource;


    @Autowired
    public ReportService(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<SubscriptionStatusMeasure> getSubscriptionStatusMeasureForMsisdn(final String msisdn) {
        return new TimedRunner<List<SubscriptionStatusMeasure>>(10, 1000) {

            @Override
            protected List<SubscriptionStatusMeasure> run() {
                List<SubscriptionStatusMeasure> subscriptionStatusMeasures = null;
                try {
                    String query = "select ssm.status,s.msisdn,s.name,s.age_of_beneficiary,s.estimated_date_of_delivery,s.date_of_birth,spd.subscription_pack,cd.channel " +
                            "from report.subscription_status_measure ssm " +
                            "join report.subscriptions sc on sc.id = ssm.subscription_id " +
                            "join report.subscribers s on s.id=sc.subscriber_id " +
                            "join report.channel_dimension cd on cd.id = sc.channel_id " +
                            "join report.subscription_pack_dimension spd on spd.id = sc.subscription_pack_id where msisdn = " + msisdn;
                    subscriptionStatusMeasures = executeQuery(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return subscriptionStatusMeasures;
            }

            private List<SubscriptionStatusMeasure> executeQuery(String query) throws SQLException {


                try (Connection connection = dataSource.getConnection();
                     Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery(query)) {
                    List<SubscriptionStatusMeasure> subscriptionStatusMeasures = new ArrayList<>();
                    while (resultSet.next()) {
                        String msisdn = resultSet.getString("msisdn");
                        String status = resultSet.getString("status");
                        String pack = resultSet.getString("subscription_pack");
                        String channel = resultSet.getString("channel");
                        String name = resultSet.getString("name");
                        String age = resultSet.getString("age_of_beneficiary");
                        String estimatedDateOfDelivery = resultSet.getString("estimated_date_of_delivery");
                        String dateOfBirth = resultSet.getString("date_of_birth");
                        subscriptionStatusMeasures.add(new SubscriptionStatusMeasure(msisdn, status, pack, channel, name, age, estimatedDateOfDelivery, dateOfBirth));
                    }
                    return subscriptionStatusMeasures.size() == 2 ? subscriptionStatusMeasures : null;
                }
            }
        }.executeWithTimeout();
    }

    public void createNewLocation(String district, String block, String panchayat) throws SQLException {

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("insert into report.location_dimension(district,block,panchayat) values ('" + district + "','" + block + "','" + panchayat + "')");
        }

    }

    public void deleteAll() throws SQLException {

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("delete from report.subscription_status_measure");
            statement.execute("delete from report.subscriptions");
            statement.execute("delete from report.subscribers");
            statement.execute("delete from report.location_dimension");
        }

    }
}