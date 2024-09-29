using Microsoft.AspNetCore.Mvc;

namespace SignalRTest.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class WeatherForecastController : ControllerBase
    {
        private static readonly string[] Summaries = new[]
        {
            "Freezing", "Bracing", "Chilly", "Cool", "Mild", "Warm", "Balmy", "Hot", "Sweltering", "Scorching"
        };

        private readonly ILogger<WeatherForecastController> _logger;

        public WeatherForecastController(ILogger<WeatherForecastController> logger)
        {
            _logger = logger;
        }

        [HttpGet(Name = "GetWeatherForecast")]
        public IEnumerable<WeatherForecast> Get()
        {
            _logger.LogInformation("get");
            return Enumerable.Range(1, 5).Select(index => new WeatherForecast
            {
                Date = DateTime.Now.AddDays(index),
                TemperatureC = Random.Shared.Next(-20, 55),
                Summary = Summaries[Random.Shared.Next(Summaries.Length)]
            })
            .ToArray();
        }

        [HttpDelete]
        public bool Delete(int id)
        {
            return true;
        }

        [HttpPut]
        public bool Put() 
        {
            return false;
        }

        [HttpPost]
        public bool Post() 
        {
            return false;
        }

        [HttpGet("Test")]
        public bool Test(bool para) 
        {
            _logger.LogInformation($"Test: para = {para}");
            return !para;
        }

        public async Task<ActionResult> getD() 
        {
            return Ok();
        }
    }
}