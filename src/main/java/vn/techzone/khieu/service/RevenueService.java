package vn.techzone.khieu.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.response.order.ResMonthlyRevenueDTO;
import vn.techzone.khieu.dto.response.order.ResOrderCountDTO;
import vn.techzone.khieu.dto.response.order.ResRevenue.ResRevenue;
import vn.techzone.khieu.dto.response.order.ResRevenue.ResRevenueThisMonthDTO;
import vn.techzone.khieu.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class RevenueService {
    private final OrderRepository orderRepository;

    public ResOrderCountDTO countOrders() {
        ZoneId vnZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now(vnZone);
        Instant start = now.withDayOfMonth(1).toLocalDate().atStartOfDay(vnZone).toInstant();
        Instant end = now.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate()
                .atTime(23, 59, 59).atZone(vnZone).toInstant();
        return orderRepository.countOrders(start, end);
    }

    public ResRevenueThisMonthDTO getRevenueThisMonth() {
        ZoneId vnZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now(vnZone);

        Instant startCurrent = now.withDayOfMonth(1).toLocalDate().atStartOfDay(vnZone).toInstant();
        Instant endCurrent = now.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate()
                .atTime(23, 59, 59).atZone(vnZone).toInstant();
        Instant startPrev = now.minusMonths(1).withDayOfMonth(1).toLocalDate().atStartOfDay(vnZone).toInstant();
        Instant endPrev = now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).toLocalDate()
                .atTime(23, 59, 59).atZone(vnZone).toInstant();

        ResRevenue result = orderRepository.getRevenue(startCurrent, endCurrent, startPrev, endPrev);

        long current = result.getCurrentMonthRevenue() != null ? result.getCurrentMonthRevenue() : 0;
        long prev = result.getPrevMonthRevenue() != null ? result.getPrevMonthRevenue() : 0;

        double growth = 0;
        if (prev > 0) {
            growth = ((double) (current - prev) / prev) * 100;
        } else if (current > 0) {
            growth = 100;
        }

        ResRevenueThisMonthDTO res = new ResRevenueThisMonthDTO(current, Math.round(growth * 100.0) / 100.0);
        return res;
    }

    public List<Long> getRevenueByMonth() {
        ZoneId vnZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now(vnZone);

        Instant start = now.withDayOfYear(1).toLocalDate().atStartOfDay(vnZone).toInstant();
        Instant end = now.with(TemporalAdjusters.lastDayOfYear()).toLocalDate()
                .atTime(23, 59, 59).atZone(vnZone).toInstant();

        List<ResMonthlyRevenueDTO> result = orderRepository.getRevenueByMonth(start, end);

        Long[] monthlyRevenue = new Long[12];
        Arrays.fill(monthlyRevenue, 0L);

        result.forEach(r -> monthlyRevenue[r.getMonth() - 1] = r.getRevenue());

        return Arrays.asList(monthlyRevenue);
    }
}
