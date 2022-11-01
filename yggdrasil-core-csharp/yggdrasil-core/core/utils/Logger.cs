using static yggdrasil_core.core.utils.Logger;

namespace yggdrasil_core.core.utils
{
    public class Logger
    {
        public static Logger Instance(Type type) => new Logger(type);
        public static Logger Instance(Object type) => new Logger(type.GetType());

        private Type _type;

        public Logger(Type type)
        {
            _type = type;
        }

        public void Trace(String value)
        {
            Log(Level.TRACE, value);
        }

        public void Info(String value)
        {
            Log(Level.INFO, value);
        }

        public void Debug(String value)
        {
            Log(Level.DEBUG, value);
        }

        public void Warn(String value)
        {
            Log(Level.WARN, value);
        }

        public void Error(String value)
        {
            Log(Level.ERROR, value);
        }

        private void Log(Level level, String value)
        {
            switch (level) {
                case Level.TRACE:
                case Level.INFO:
                case Level.DEBUG:
                    Console.ForegroundColor = ConsoleColor.White;
                    break;
                case Level.WARN:
                    Console.ForegroundColor = ConsoleColor.Yellow;
                    break;
                case Level.ERROR:
                    Console.ForegroundColor = ConsoleColor.Red;
                    break;
            }
            // Log str
            /*
            String.Format("{0} {1} {2}: {3}", 
            DateTimeUtil.GetCurrentTimestamp().ToString("yyyy'-'MM'-'dd'T'HH':'mm':'ss.fffffffK"),
                _type.ToString(), 
                level.ToString(), 
                value.ToString())
            */
            Console.WriteLine(String.Format("{0} {1} {2}: {3}", 
                DateTimeUtil.GetCurrentTimestamp().ToString("yyyy'-'MM'-'dd'T'HH':'mm':'ss.fffffffK"),
                TypeString(_type), 
                level.ToString(), 
                value.ToString()));
        }

        private static String TypeString(Type type)
        {
            String[] typeStr = type.ToString().Split(".");
            String returnStr = "";
            for(int i = 0; i < typeStr.Length; i++)
            {
                if (i != typeStr.Length - 1) 
                {
                    returnStr += (typeStr[i][0] + ".");
                }
                else
                {
                    returnStr += typeStr[i];
                }
            }
            return returnStr;
        }

        private enum Level
        {
            ERROR,
            WARN,
            DEBUG,
            INFO,
            TRACE
        }
    }
}
