namespace yggdrasil_core.core.utils
{
    public class DateTimeUtil
    {
        public static DateTime GetCurrentTimestamp()
        {
            return DateTime.UtcNow;
        }

        public static DateTime FromMessageTimeStamp(int timestamp)
        {
            DateTimeOffset dto = DateTimeOffset.FromUnixTimeSeconds(timestamp);
            return dto.DateTime;
        }

        public static long toEpockSecondTimeStamp(DateTime dateTime)
        {
            DateTimeOffset dto = new DateTimeOffset(dateTime);
            return dto.ToUnixTimeSeconds();
        }
    }
}
