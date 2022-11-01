using yggdrasil_core.core.utils;

namespace yggdrasil_core
{
    public class Program
    {
        private static readonly Logger logger = Logger.Instance(typeof(Program));
        public static void Main(string[] args)
        {
            logger.Info("Starting Yggdrasil Core...");
            var builder = WebApplication.CreateBuilder(args);

            // Add services to the container.
            logger.Info("Adding controllers and registering dependancies.");
            builder.Services.AddControllers();
            builder.Services.AddEndpointsApiExplorer();
            builder.Services.AddSwaggerGen();

            logger.Info("Loading the application.");
            var app = builder.Build();

            // Configure the HTTP request pipeline.
            if (app.Environment.IsDevelopment())
            {
                logger.Info("Development mode is active.");
                app.UseSwagger();
                app.UseSwaggerUI();
            }

            logger.Info("Setting the authorization.");
            app.UseAuthorization();
            app.MapControllers();
            logger.Info("Starting ASP_NET.");
            app.Run();
        }
    }
}