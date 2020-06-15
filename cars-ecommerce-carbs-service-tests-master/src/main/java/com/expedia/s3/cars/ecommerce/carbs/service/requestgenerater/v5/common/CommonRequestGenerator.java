package com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.v5.common;

import com.expedia.cars.schema.common.v1.ClientIdentityInfo;
import com.expedia.cars.schema.common.v1.MessageInfo;
import com.expedia.cars.schema.common.v1.PointOfSaleKey;
import com.expedia.cars.schema.common.v1.TraceInfo;
import com.expedia.cars.schema.ecommerce.shopping.v1.CarTransportationSegment;
import com.expedia.s3.cars.framework.test.common.constant.RequestDefaultValues;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.TestTimeData;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.util.StringUtils;

import java.util.Random;
import java.util.UUID;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
@SuppressWarnings("PMD")
public class CommonRequestGenerator
{
    public static MessageInfo createMessageInfo(String messageName, String userGUID, TestData testData)
    {
        final MessageInfo messageInfo = new MessageInfo();
        messageInfo.setMessageGUID(UUID.randomUUID().toString());
        messageInfo.setUserGUID(userGUID);
        messageInfo.setClientIdentityInfo(createClientIdentityInfo(testData));
        try
        {
            messageInfo.setCreateDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    new DateTime().toGregorianCalendar()));
        }
        catch (Exception e)
        {
            //NOP
        }
        messageInfo.setMessageName(messageName);
        messageInfo.setTransactionGUID(UUID.randomUUID().toString());

        return messageInfo;
    }

    public static ClientIdentityInfo createClientIdentityInfo(TestData testData)
    {
        final ClientIdentityInfo clientIdentityInfo = new ClientIdentityInfo();
        if (StringUtils.hasText(testData.getClientCode()))
        {
            clientIdentityInfo.setClientCode(testData.getClientCode());
        }
        else
        {
            clientIdentityInfo.setClientCode("TEST123");
        }
        clientIdentityInfo.setPreferredLocale("en_US");

        final TestScenario testScenario = testData.getScenarios();
        clientIdentityInfo.setPointOfSaleKey(createPointOfSaleKey(testData.getScenarios()));

        return clientIdentityInfo;
    }

    public static TraceInfo createTraceInfo()
    {
        final TraceInfo traceInfo = new TraceInfo();
        traceInfo.setSessionGUID(UUID.randomUUID().toString());
        traceInfo.setSendingHostname("CarBSAutomationTestTestNG");
        traceInfo.setEnableDebugTrace(false);
        traceInfo.setForceLogging(true);
        traceInfo.setForceDownstreamTransactions(true);

        return traceInfo;
    }

    public static CarTransportationSegment createCarTransportationSegment(TestData testData)
    {
        final CarTransportationSegment carTransportationSegment = new CarTransportationSegment();
        carTransportationSegment.setPickUpAirportCode(testData.getScenarios().getPickupLocationCode());
        carTransportationSegment.setDropOffAirportCode(testData.getScenarios().getDropOffLocationCode());
        createSegmentStartEndDateTime(carTransportationSegment, testData);

        return carTransportationSegment;
    }

    public static void createSegmentStartEndDateTime(CarTransportationSegment carTransportationSegment, TestData testData)
    {
        if (null == testData.getUseDays())
        {
            //TODO: currently added basic datetime.  We need to support for different type of datetime
            //TODO: that is used in regression cases.
            DateTime startDate = DateTime.now();
            if (testData.isMerchantBoolean())
            {
                startDate = startDate.plusDays(RequestDefaultValues.SEARCH_START_INVERVAL_DAYS_MERCHANT);
            }
            else
            {
                startDate = startDate.plusDays(RequestDefaultValues.SEARCH_START_INTERVAL_DAYS);
            }
            carTransportationSegment.setStartDateTime(createDateTime(startDate));

            DateTime endDate = startDate.plusDays(RequestDefaultValues.SEARCH_USE_DAYS);
            carTransportationSegment.setEndDateTime(createDateTime(endDate));
        }
        else
        {
            final TestTimeData testTimeData = new TestTimeData(testData.getUseDays(), testData.isMerchantBoolean(),
                    testData.isWeekendBoolean(), testData.isExtraHours());
            // set the time
            int startDateFromNow = testTimeData.getSearchStartDay();
            int endDateFromNow = testTimeData.getSearchStartDay() + testTimeData.getDurationDays();
            int startHour = 10;
            int endHour = 10;

            // handle extra hours
            if (testTimeData.isExtraHoursBool())
            {
                startHour = 22;
                endHour = 24;
            }

            // handle weekend
            if (testTimeData.isWeekendBool())
            {
                final DateTime startDate = DateTime.now().plusDays(startDateFromNow);

                if (startDate.getDayOfWeek() != DateTimeConstants.FRIDAY
                        && startDate.getDayOfWeek() != DateTimeConstants.SATURDAY)
                {
                    if (testTimeData.getDurationDays() == 3)
                    {
                        startDateFromNow = startDateFromNow + (DateTimeConstants.FRIDAY - startDate.getDayOfWeek());
                        endDateFromNow = startDateFromNow + 3;
                    }
                    else if (testTimeData.getDurationDays() == 2)
                    {
                        final Random rand = new Random();
                        //for weekend two days, it may start form Friday or Saturday
                        final int randNum = rand.nextInt(1);
                        startDateFromNow =
                                startDateFromNow + DateTimeConstants.FRIDAY - startDate.getDayOfWeek() + randNum;
                        endDateFromNow = startDateFromNow + 2;
                    }
                    else if (testTimeData.getDurationDays() == 1)
                    {
                        //For weekend 1 days, there is also possible that it start from Sunday(time < 12:00),
                        //then add extra hours, but end time must <= 12 on Monday.
                        final Random rand = new Random();
                        final int randNum = rand.nextInt(2);
                        startDateFromNow =
                                startDateFromNow + (DateTimeConstants.FRIDAY - startDate.getDayOfWeek()) + randNum;
                        endDateFromNow = startDateFromNow + 1;
                    }
                }
            }

            final DateTime dateTimeNow = DateTime.now().plusHours(12 - DateTime.now().getHourOfDay())
                    .plusSeconds(20 - DateTime.now().getSecondOfMinute());
            DateTime startDateTime = dateTimeNow.plusDays(startDateFromNow);

            if (0 != startHour)
            {
                startDateTime = startDateTime.plusHours(startHour - startDateTime.getHourOfDay());
            }
            carTransportationSegment.setStartDateTime(createDateTime(startDateTime));

            DateTime endDateTime = dateTimeNow.plusDays(endDateFromNow);

            if (0 != endHour)
            {
                endDateTime = endDateTime.plusHours(endHour - endDateTime.getHourOfDay());
            }

            carTransportationSegment.setEndDateTime(createDateTime(endDateTime));
        }
    }

    public static XMLGregorianCalendar createDateTime(DateTime dateTime)
    {
        XMLGregorianCalendar calendar  = null;

        try
        {
            calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTime.toGregorianCalendar());
        }
        catch (Exception e)
        {
            //NOP
        }

        return calendar;
    }

    public static PointOfSaleKey createPointOfSaleKey(TestScenario testScenario)
    {
        final PointOfSaleKey pointOfSaleKey = new PointOfSaleKey();
        pointOfSaleKey.setManagementUnitCode(testScenario.getManagementUnitCode());
        pointOfSaleKey.setCompanyCode(testScenario.getCompanyCode());
        pointOfSaleKey.setJurisdictionCountryCode(testScenario.getJurisdictionCountryCode());

        return pointOfSaleKey;
    }
}
