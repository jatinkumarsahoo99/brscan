package vraj.app.cancerscreening.screening.data.util

import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.DAY_OF_MONTH

object DateUtil {

    const val DATE_FORMAT = "dd/MM/yyyy"
    const val SIMPLE_DATE_FORMAT = "dd/MM/yyyy'T'HH:mm:ss'Z'"
    const val DATE_FORMAT_WITH_TIME = "dd-MM-yyyy'T'HH:mm:ss'Z'"
    const val SERVER_INPUT_DATE_FORMAT = "yyyy-MM-dd"
    const val SERVER_OUTPUT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    const val SIMPLE_DATE_TIME_FORMAT = "E, MMM dd yyyy HH:mm a"
    const val UTC = "UTC"

    const val REPORT_DATE_FORMAT = "MMM dd, yyyy"
    const val REPORT_TIME_FORMAT = "HH:mm a"

    fun getSimpleDateFormat(dateFormat: String) : SimpleDateFormat
    {
        return SimpleDateFormat(dateFormat)
    }

    fun getDate(calendar: Calendar, strFormatter: String): String {
        return getSimpleDateFormat(strFormatter).format(calendar.time)
    }

    fun getDateWithTime(calendar: Calendar): String {
        return getSimpleDateFormat(DATE_FORMAT_WITH_TIME).format(calendar.time)
    }

    fun getDateFromTimestamp(timestamp: Long): String {
        return DateFormat.format(DATE_FORMAT, Date(timestamp)).toString()
    }

    fun getTimeStamp(): String {
        val calendar = Calendar.getInstance()
        val df = getSimpleDateFormat(DATE_FORMAT_WITH_TIME)
        df.timeZone = TimeZone.getTimeZone(UTC)
        return df.format(calendar.time)
    }

    fun getDateFromString(strDate: String, strFormatter: String) : Date
    {
        return getSimpleDateFormat(strFormatter).parse(strDate)
    }

    fun getDateInServerFormat(parserFormat: String, strDate: String) : String
    {
        val parser = getSimpleDateFormat(parserFormat)
        return getSimpleDateFormat(SERVER_OUTPUT_DATE_FORMAT).format(parser.parse(strDate))
    }

    fun getDateInSimpleFormat(parserFormat: String, strDate: String) : String
    {
        val parser = getSimpleDateFormat(parserFormat)
        return getSimpleDateFormat(SIMPLE_DATE_TIME_FORMAT).format(parser.parse(strDate))
    }

    fun getCreatedDate() : String
    {
        return getSimpleDateFormat(SERVER_OUTPUT_DATE_FORMAT).format(Date())
    }

    fun calculateAge(dateOfBirth: Date) : Int
    {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob.time = dateOfBirth

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(DAY_OF_MONTH) < dob.get(DAY_OF_MONTH))
        {
            age--
        }

        return age
    }

    fun getTimeFromValues(hourOfDay: Int, minutesOfDay: Int) : String
    {
        var strTime = ""

        if(hourOfDay < 10)
            strTime += "0$hourOfDay"
        else
            strTime += "$hourOfDay"

        if(minutesOfDay < 10)
            strTime += ":0$minutesOfDay"
        else
            strTime += ":$minutesOfDay"

        return strTime
    }

    fun getDateFromValues(dayOfMonth: Int, month: Int, year: Int) : String
    {
        var strDate = ""

        if(dayOfMonth < 10)
            strDate += "0$dayOfMonth"
        else
            strDate += "$dayOfMonth"

        if(month < 10)
            strDate += "/0$month"
        else
            strDate += "/$month"

        strDate += "/$year"

        return strDate
    }
}