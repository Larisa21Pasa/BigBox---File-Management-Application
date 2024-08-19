using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AdminApp
{
    public class User
    {
        [JsonProperty("userId")]
        public int Id;
        [JsonProperty("name")]
        public string Name;
        [JsonProperty("email")]
        public string Email;
        [JsonProperty("hashedPassword")]
        public string Password;
        [JsonProperty("isBlocked")]
        public bool isBlocked;
        [JsonProperty("subscription")] //type Subscription
        public Subscription subscription;
        [JsonProperty("roleEnum")]
        public string role;
        [JsonProperty("tokens")]
        public Object tokens;

    

    }
}
