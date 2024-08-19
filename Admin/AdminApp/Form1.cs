using System;
using System.CodeDom;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using static System.Windows.Forms.VisualStyles.VisualStyleElement;
using static System.Windows.Forms.VisualStyles.VisualStyleElement.ListView;

namespace AdminApp
{
    public partial class Form1 : Form
    {
        [DllImport("Gdi32.dll", EntryPoint = "CreateRoundRectRgn")]
        private static extern IntPtr CreateRoundRectRgn
      (
          int nLeftRect,     // x-coordinate of upper-left corner
          int nTopRect,      // y-coordinate of upper-left corner
          int nRightRect,    // x-coordinate of lower-right corner
          int nBottomRect,   // y-coordinate of lower-right corner
          int nWidthEllipse, // width of ellipse
          int nHeightEllipse // height of ellipse
      );
       
        private Point startPoint = new Point(0, 0);     // also for the moving
        private int dragSize = 300;
        private bool isDragging = false;

        public Form1()
        {
            InitializeComponent();
            InitializeTimer();
            this.CenterToScreen();
            this.FormBorderStyle = FormBorderStyle.None;
            Region = Region.FromHrgn(CreateRoundRectRgn(0, 0, Width, Height, 20, 20));
            this.usernameTextBox.GotFocus += new EventHandler(RemoveTextUsername);
            this.passwordTextBox.GotFocus += new EventHandler(RemoveTextPassword);

            this.usernameTextBox.LostFocus += new EventHandler(AddTextUsername);
            this.passwordTextBox.LostFocus += new EventHandler(AddTextPassword);
            // MyInitializer();
            this.MouseDown += MainForm_MouseDown;
            this.MouseMove += MainForm_MouseMove;
            this.MouseUp += MainForm_MouseUp;
            crownDockPanel1.MouseUp += MainForm_MouseUp;
            crownDockPanel1.MouseMove += MainForm_MouseMove;
            crownDockPanel1.MouseDown += MainForm_MouseDown;
            //
        }

        private void CrownDockPanel1_MouseDown(object sender, MouseEventArgs e)
        {
            throw new NotImplementedException();
        }

        private void MainForm_MouseDown(object sender, MouseEventArgs e)
        {
            if (e.Y > (this.Location.Y-this.Height) && e.Y < ((this.Location.Y - this.Height+53) + dragSize))
            {
                this.startPoint = e.Location;
                isDragging = true;
            }
        }

        private void MainForm_MouseMove(object sender, MouseEventArgs e)
        {
            if (isDragging)
            {
                Point p1 = new Point(e.X, e.Y);
                Point p2 = this.PointToScreen(p1);
                Point p3 = new Point(p2.X - this.startPoint.X,
                                     p2.Y - this.startPoint.Y);
                this.Location = p3;
            }
        }

        private void MainForm_MouseUp(object sender, MouseEventArgs e)
        {
            isDragging = false;
        }
                
        public void RemoveTextUsername(object sender, EventArgs e)
        {
            if (usernameTextBox.Text == "Email")
            {
                usernameTextBox.Text = "";
            }
        }

        public void RemoveTextPassword(object sender, EventArgs e)
        {
            if (passwordTextBox.Text == "Password")
            {
                passwordTextBox.Text = "";
                passwordTextBox.PasswordChar = '*';
            }
        }

        public void AddTextUsername(object sender, EventArgs e)
        {
            if (string.IsNullOrWhiteSpace(usernameTextBox.Text))
                usernameTextBox.Text = "Email";
        }

        public void AddTextPassword(object sender, EventArgs e)
        {
            if (string.IsNullOrWhiteSpace(passwordTextBox.Text))
            {

                passwordTextBox.Text = "Password";
                passwordTextBox.PasswordChar = '\0';
            }
        }

      

        void CloseLoginOpenMenu() {
            Program.menu = new Menu(userResponse);
            Program.menu.Show();
            Close();
        }

        private Timer notifTimer;

        private void InitializeTimer()
        {
            notifTimer = new Timer();
            notifTimer.Interval = 2000; // 2000 milliseconds = 2 seconds
            notifTimer.Tick += closeNotif;
        }
        private void closeNotif(object sender, EventArgs e)
        {
            hopeNotify1.Hide();
            notifTimer.Stop();
        }

        User userResponse;
        private async void foreverButtonSticky1_ClickAsync(object sender, EventArgs e)
        {
           string email=usernameTextBox.Text;
            string password=passwordTextBox.Text;


                try{
                    string loginUrl = "http://localhost:8080/api/auth/authenticate";
                    
                    var data = new {  email,  password };
                    string serializedData = Newtonsoft.Json.JsonConvert.SerializeObject(data);
                    var content = new StringContent(serializedData, System.Text.Encoding.UTF8, "application/json");
                    HttpResponseMessage loginResponse = await Program.client.PostAsync(loginUrl, content);
                    if (!loginResponse.IsSuccessStatusCode) {
                        hopeNotify1.Show();
                        hopeNotify1.Text = "Date de autentificare gresite!";
                        notifTimer.Start();
                        return;
                }
                    
                    AuthResponse authResp = Newtonsoft.Json.JsonConvert.DeserializeObject<AuthResponse>(await loginResponse.Content.ReadAsStringAsync());
                    Program.accessToken = authResp.AccessToken;
                    Program.refreshToken = authResp.RefreshToken;
                    Console.WriteLine("Access Token: " + Program.accessToken);
                    Console.WriteLine("Refresh Token: " + Program.refreshToken);


                    string roleUrl= "http://localhost:8080/api/users/user/" + email;
                    Program.client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", Program.accessToken);
                    HttpResponseMessage roleResponse = await Program.client.GetAsync(roleUrl);
                    if (!loginResponse.IsSuccessStatusCode)
                    {
                        hopeNotify1.Show();
                        hopeNotify1.Text = "Momentan server-ul nu poate fii accesat!";
                        notifTimer.Start();
                        return;
                    }
                    Console.WriteLine(await roleResponse.Content.ReadAsStringAsync());

                    userResponse = Newtonsoft.Json.JsonConvert.DeserializeObject<User>(await roleResponse.Content.ReadAsStringAsync());
                    if (userResponse.role == "ADMIN") {
                        Console.WriteLine("Logged");

                        CloseLoginOpenMenu();
                    }
                    else{
                        hopeNotify1.Show();
                        hopeNotify1.Text = "Nu exista Admin cu aceste date de autentificare!";
                        notifTimer.Start();
                    }
                    

                }
                catch (Exception ex)
                {
                hopeNotify1.Show();
                hopeNotify1.Text =ex.Message;
                notifTimer.Start();
                }
            
        }


        [DllImport("kernel32.dll", SetLastError = true)]
        [return: MarshalAs(UnmanagedType.Bool)]
        static extern bool AllocConsole();

        private void foreverClose1_Click(object sender, EventArgs e) ////////////////////////////////////////////////////////////
        {
            /*var confirmResult = MessageBox.Show("Esti sigur ca vrei sa iesi ?",
                                     "Confirm Exit!!",
                                     MessageBoxButtons.YesNo);
            if (confirmResult == DialogResult.Yes)
            {
                Application.Exit();
            }*/
           
           Application.Exit();
        }
    }
}
