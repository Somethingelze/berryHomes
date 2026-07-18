package net.berryhomes.model.dto;

import java.util.List;

public record DashboardStatsDto(
        long totalNewLeads,
        long totalProjects,
        long tenantLeadsCount,
        long homeownerLeadsCount,
        long investorLeadsCount,
        List<ContactDto> recentLeads
) {}
