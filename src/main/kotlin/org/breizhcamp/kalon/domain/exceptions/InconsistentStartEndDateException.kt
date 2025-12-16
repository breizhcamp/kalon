package org.breizhcamp.kalon.domain.exceptions

import java.time.LocalDate

class InconsistentStartEndDateException(startDate: LocalDate, endDate: LocalDate):
    RuntimeException("Inconsistent dates: start date $startDate is after end date $endDate")
