using Microsoft.VisualBasic;
using Microsoft.VisualBasic.ApplicationServices;
using Newtonsoft.Json;
using ReaLTaiizor.Controls;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Windows.Forms.VisualStyles;
using static System.Windows.Forms.VisualStyles.VisualStyleElement;
using static System.Windows.Forms.VisualStyles.VisualStyleElement.ListView;

namespace AdminApp
{
    public partial class Menu : Form
    {

        int circleX;
        int circleY;
        string currentUserName ;
        User currentAdmin;
   
        public Menu(User admin)
        {
            InitializeComponent();
            getDataAsync();



            this.CenterToScreen();


            circleX=parrotPictureBox1.Width/2;
            circleY=parrotPictureBox1.Height/2;
            this.currentAdmin = admin;
            currentUserName = admin.Name;

            nightLabel1.Text = admin.Name;
            nightLabel2.Text = "BigBox";

            setSecondTabActive();




        }
        List<User> users;
        private async Task getDataAsync() {
            HttpResponseMessage res = await Program.client.GetAsync("http://localhost:8080/api/users");
            users = Newtonsoft.Json.JsonConvert.DeserializeObject<List<User>>(await res.Content.ReadAsStringAsync());
            
            listView1.Columns.Add("Id",  Convert.ToInt32(listView1.Width*0.1), HorizontalAlignment.Left);
            listView1.Columns.Add("Name", Convert.ToInt32(listView1.Width * 0.2), HorizontalAlignment.Left);
            listView1.Columns.Add("Email", Convert.ToInt32(listView1.Width * 0.29), HorizontalAlignment.Left);
            listView1.Columns.Add("IsBlocked", Convert.ToInt32(listView1.Width * 0.2), HorizontalAlignment.Left);
            listView1.Columns.Add("Role", Convert.ToInt32(listView1.Width * 0.1), HorizontalAlignment.Left);
            listView1.Columns.Add("Plan", Convert.ToInt32(listView1.Width * 0.108), HorizontalAlignment.Left);


            //TODO pus numa userii, nu si admini
            foreach (User u in users)
            {
                if (u.role == "ADMIN") { continue; }
                listView1.Items.Add(new ListViewItem(new string[] { u.Id.ToString(), u.Name, u.Email, u.isBlocked.ToString(), u.role,u.subscription.plan.planName }));
            }

            listView1.BackColor = System.Drawing.Color.LightGray;

            listView1.HeaderStyle = ColumnHeaderStyle.Nonclickable;


            foreach (ListViewItem item in listView1.Items)
            {
                item.BackColor = System.Drawing.Color.DarkGray;
            }
            listView1.DrawItem += listView1_DrawItem;

            res = await Program.client.GetAsync("http://localhost:8080/api/file_manager/global/details");
            var properties= Newtonsoft.Json.JsonConvert.DeserializeObject<FileManagerDetails>(await res.Content.ReadAsStringAsync());
            Console.Out.WriteLine(properties.totalOcupiedSize);
   
            dreamTextBox2.Text = "Nr useri: "+properties.noUsers+"\r\nSpatiu total ocupat: "+properties.totalOcupiedSize+" B\r\n"+"Numarul total de fisiere: "+properties.noFiles;
            dreamTextBox2.DeselectAll();
            dreamTextBox2.Select(0, 0);
            dreamTextBox2.Focus();
        }
        public class FileManagerDetails
        {
            public int totalOcupiedSize { get; set; }
            public int noFiles { get; set; }
            public int noUsers { get; set; }
        }

        private void setFirstTabActive() {
            this.materialTabControl1.SelectTab(1);

            nightPanel2.Side = ReaLTaiizor.Controls.NightPanel.PanelSide.Right;
            nightPanel3.Side = ReaLTaiizor.Controls.NightPanel.PanelSide.Left;

            pictureBox2.BackColor = nightPanel2.RightSideColor;
            pictureBox3.BackColor = nightPanel3.LeftSideColor;

        }

        private void setSecondTabActive() {
            this.materialTabControl1.SelectTab(0);
            nightPanel2.Side = ReaLTaiizor.Controls.NightPanel.PanelSide.Left;
            nightPanel3.Side = ReaLTaiizor.Controls.NightPanel.PanelSide.Right;

            pictureBox2.BackColor = nightPanel2.LeftSideColor;
            pictureBox3.BackColor = nightPanel3.RightSideColor;
        }

        private void tabPage2_Click(object sender, EventArgs e)
        {

        }

        private void nightPanel2_Paint(object sender, PaintEventArgs e)
        {

        }

        private void click_panel_1(object sender, EventArgs e)
        {
            setFirstTabActive();

        }

        private void click_panel_2(object sender, EventArgs e)
        {
           setSecondTabActive();
        }




        private bool hiddenSideBar=false;

        private void sidebar_click(object sender, EventArgs e)
        {

            if (hiddenSideBar == false)
            {
                nightPanel1.Visible = false;
                this.materialTabControl1.Size = new System.Drawing.Size(1147, 744); // initial
                this.materialTabControl1.Location = new System.Drawing.Point(0, 30);

                this.materialTabControl1.Size = new System.Drawing.Size(1200, 744);
                
                hiddenSideBar = true;
            }
            else
            {
                nightPanel1.Visible = true;
                this.materialTabControl1.Location = new System.Drawing.Point(53, 30);
                this.materialTabControl1.Size = new System.Drawing.Size(1147, 744); // initial

                
                hiddenSideBar=false;
            }



        }

      
        private async void listViewSimple_ClickAsync(object sender, MouseEventArgs e)
        {
            for (int i = 0; i < listView1.Items.Count; i++)
            {
                var rectangle = listView1.GetItemRect(i);
                if (rectangle.Contains(e.Location))
                {

                    ListViewItem selectedItem = listView1.SelectedItems[0];

                    int id = int.Parse(selectedItem.SubItems[0].Text);
                    bool selectedIsBlocked = bool.Parse(selectedItem.SubItems[3].Text);
                    string planName =selectedItem.SubItems[5].Text;
                    Console.WriteLine("Id selectat"+id);

                    ModifForm modif =new ModifForm(id, selectedIsBlocked, planName);
                    if (modif.ShowDialog() == DialogResult.OK)
                    {

                        Console.WriteLine(modif.userId + "   "+modif.isBlocked+"   "+modif.plan);
                        
                        listView1.SelectedItems[0].SubItems[3].Text = modif.isBlocked.ToString();
                        listView1.SelectedItems[0].SubItems[5].Text = modif.plan.ToString();

                        if (modif.isBlocked != selectedIsBlocked) {
                            LogAction("Utilizatorul " + modif.userId + " a devenit"+ modif.isBlocked);
                        }

                        if (modif.plan != planName) {
                            LogAction("Planul utilizatorului " + modif.userId + " a fost schimbat din "+planName+" in "+modif.plan+" .");
                        }
                        User selectedUser = null;

                        for (int j = 0; j < users.Count; j++) {
                            if (users[i].Id == id) {
                                selectedUser = users[i]; break;
                            }
                        }
                        int planId = 1;

                        switch (modif.plan){
                            case "Basic":
                                planId = 1;
                                 break;

                            case "Standard":
                                planId = 2;

                                break;
                            case "Premium":
                                planId = 3;
                                break;
                            default: break;
                        }
                      

                        selectedUser.isBlocked = modif.isBlocked;
                        selectedUser.subscription.plan.Id = 1;

                        string updateUser = "http://localhost:8080/api/admin/users/" + modif.userId.ToString()+"?isBlocked="+modif.isBlocked.ToString();
                        StringContent content = new StringContent(modif.isBlocked.ToString().ToLower(), Encoding.UTF8, "application/json");
                        HttpResponseMessage response = await Program.client.PostAsync(updateUser,content);
                        Console.WriteLine(await response.Content.ReadAsStringAsync());


                        
                        string updatePlan = "http://localhost:8080/api/users/" + modif.userId.ToString() + "/plan";
                        content = new StringContent(planId.ToString().ToLower(), Encoding.UTF8, "application/json");
                        response = await Program.client.PostAsync(updatePlan, content);
                        Console.WriteLine(await response.Content.ReadAsStringAsync());

                        Console.WriteLine("S-au salvat schimbarile");
                    }
                    return;
                }
            }

            MessageBox.Show("None");
        }


        private void nightFrom_SizeChnaged(object sender, EventArgs e)
        {
            LogAction("Resize");
            Console.WriteLine(listView1);
            if (listView1.Items.Count >0)
            {
                listView1.Columns[0].Width = Convert.ToInt32(listView1.Width * 0.1);
                listView1.Columns[1].Width = Convert.ToInt32(listView1.Width * 0.2);
                listView1.Columns[2].Width = Convert.ToInt32(listView1.Width * 0.29);
                listView1.Columns[3].Width = Convert.ToInt32(listView1.Width * 0.2);
                listView1.Columns[4].Width = Convert.ToInt32(listView1.Width * 0.1);
                listView1.Columns[5].Width = Convert.ToInt32(listView1.Width * 0.108);
            }
        }

        private void LogAction(string message=null) {

            DateTime currentDateTime = DateTime.Now;
            string formattedDateTime = currentDateTime.ToString("yyyy-MM-dd HH:mm:ss");
            dreamTextBox1.AppendText(formattedDateTime + " : (" + currentUserName + ") " + message + Environment.NewLine);
        }



        private void parrotPictureBox1_Click(object sender, EventArgs e)
        {

        }


        private void pictureBox_Paint(object sender, PaintEventArgs e)
        {
            Graphics g = e.Graphics;
            g.Clear(parrotPictureBox1.BackColor);
            int diameter = 28; 
            using (SolidBrush brush = new SolidBrush(Color.Black))
            {
                g.FillEllipse(brush, circleX - diameter / 2, circleY - diameter / 2, diameter, diameter);
            }


            string letter = currentUserName.ToUpper().Substring(0,1);// currentUser.Substring(0,1);
            Font letterFont = new Font("Arial", 9, FontStyle.Bold);
            using (SolidBrush letterBrush = new SolidBrush(Color.White))
            {
                float letterWidth = g.MeasureString(letter, letterFont).Width;
                float letterHeight = g.MeasureString(letter, letterFont).Height;
                float letterX = circleX - letterWidth / 2+1;
                float letterY = circleY - letterHeight / 2;

                g.DrawString(letter, letterFont, letterBrush, letterX, letterY);
            }
        }


        private void nightForm1_DoubleClick(object sender, EventArgs e)
        {
            if (this.WindowState == FormWindowState.Normal)
                this.WindowState = FormWindowState.Maximized;
            else
                this.WindowState = FormWindowState.Normal;
        }

        private void pictureBox1_Click(object sender, EventArgs e)
        {
            if (hiddenSideBar == false)
            {
                nightPanel4.Side = ReaLTaiizor.Controls.NightPanel.PanelSide.Right;


                pictureBox1.BackColor = nightPanel4.RightSideColor;

                nightPanel1.Visible = false ;

                this.materialTabControl1.Size = new System.Drawing.Size(1147, 744); // initial
                this.materialTabControl1.Location = new System.Drawing.Point(0, 30);

                this.materialTabControl1.Size = new System.Drawing.Size(1200, 744);

                hiddenSideBar = true;
            }
            else
            {
                nightPanel4.Side = ReaLTaiizor.Controls.NightPanel.PanelSide.Left;


                pictureBox1.BackColor = nightPanel4.LeftSideColor;
                nightPanel1.Visible = true;
                this.materialTabControl1.Location = new System.Drawing.Point(53, 30);
                this.materialTabControl1.Size = new System.Drawing.Size(1147, 744); // initial


                hiddenSideBar = false;
            }
        }

        private void listView1_DrawItem(object sender, DrawListViewItemEventArgs e)
        {

            using (Brush brush = new SolidBrush(
                       (e.State.HasFlag(ListViewItemStates.Focused)) ?
                       SystemColors.Highlight : e.Item.BackColor))
                e.Graphics.FillRectangle(brush, e.Bounds);
            e.DrawText();
            e.DrawDefault = true;
        }

        private void listView1_DrawColumnHeader(object sender, DrawListViewColumnHeaderEventArgs e)
        {
          //  e.DrawDefault = true;
            using (var brush = new SolidBrush(Color.DarkGray))
            {
                e.Graphics.FillRectangle(brush, e.Bounds);
            }

            using (var pen = new Pen(Color.White))
            {
                e.Graphics.DrawRectangle(pen, e.Bounds);
            }

            TextRenderer.DrawText(e.Graphics, e.Header.Text, e.Font, e.Bounds, Color.White, TextFormatFlags.VerticalCenter | TextFormatFlags.Left);

        }

        private void listView1_DrawSubItem(object sender, DrawListViewSubItemEventArgs e)
        {
            e.DrawDefault = true;

        }

        private void nightForm1_Click(object sender, EventArgs e)
        {

        }

        private void nightLabel1_Click(object sender, EventArgs e)
        {

        }

        private void dreamTextBox2_TextChanged(object sender, EventArgs e)
        {

        }

        private void listView1_SelectedIndexChanged(object sender, EventArgs e)
        {

        }
    }
}
