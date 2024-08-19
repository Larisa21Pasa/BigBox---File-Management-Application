using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace AdminApp
{
    internal static class Program
    {
        static public Menu menu;
        static public Form1 login;
        static public string name;
        static public string password;
        static public string accessToken;
        static public string refreshToken;
        static public HttpClient client;
            

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            client=new HttpClient();
            Form1 login = new Form1();
            login.Show();
            Application.Run();
        }
    }
}
