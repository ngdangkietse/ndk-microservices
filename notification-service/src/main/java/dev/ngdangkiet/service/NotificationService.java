package dev.ngdangkiet.service;

import com.google.protobuf.Int64Value;
import dev.ngdangkiet.dkmicroservices.common.protobuf.EmptyResponse;
import dev.ngdangkiet.dkmicroservices.notification.protobuf.PGetNotificationsRequest;
import dev.ngdangkiet.dkmicroservices.notification.protobuf.PGetNotificationsResponse;
import dev.ngdangkiet.domain.notification.alert.JsonMessage;
import dev.ngdangkiet.domain.notification.alert.PayrollJsonMessage;

/**
 * @author ngdangkiet
 * @since 11/14/2023
 */

public interface NotificationService {

    void receiveNewUserNotification(JsonMessage message);

    void receiveNewApplicantNotification(JsonMessage message);

    void receiveNewLeaveRequestNotification(JsonMessage message);

    void receiveNewUpdateLeaveRequestNotification(JsonMessage message);

    PGetNotificationsResponse getNotifications(PGetNotificationsRequest request);

    EmptyResponse seenOrUnseenNotification(Int64Value request);

    void receivePayrollNotification(PayrollJsonMessage payrollJsonMessage);
}
