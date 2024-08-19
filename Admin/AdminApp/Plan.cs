using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AdminApp
{
    public class Plan
    {
        [JsonProperty("planId")]
        public int Id;
        [JsonProperty("maxSize")]
        public long maxSize;
        [JsonProperty("namePlan")]
        public string planName;
        [JsonProperty("cloudStorage")]
        public string cloudStorage;
        [JsonProperty("planForClientType")]
        public string TypeClient;
        [JsonProperty("fitFor")]
        public string fitFor;
        [JsonProperty("description")]
        public string description;
    }
}
