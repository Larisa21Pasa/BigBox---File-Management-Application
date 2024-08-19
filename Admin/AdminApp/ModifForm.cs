using ReaLTaiizor.Controls;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace AdminApp
{
    public partial class ModifForm : Form
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

        private bool drag = false;                      // determine if we should be moving the form
        private Point startPoint = new Point(0, 0);     // also for the moving
        public int userId;
        public bool isBlocked;
        public string plan;

        public ModifForm(int userId,bool isBlocked,string plan)
        {
            this.isBlocked=isBlocked;
            this.userId = userId;
            
            
            InitializeComponent();
            MyInitializer();
            this.parrotSwitch1.SwitchState = isBlocked==true ? ParrotSwitch.State.On : ParrotSwitch.State.Off;

            for (int i = 0; i < crownComboBox1.Items.Count; i++) {

                if (crownComboBox1.Items[i].ToString() == plan ) {
                    crownComboBox1.SelectedIndex = i;
                }

            }
        }

        void MyInitializer()
        {
            crownComboBox1.Items.Add("Basic");
            crownComboBox1.Items.Add("Standard");
            crownComboBox1.Items.Add("Premium");

            this.CenterToScreen();
            this.FormBorderStyle = FormBorderStyle.None;
            Region = Region.FromHrgn(CreateRoundRectRgn(0, 0, Width, Height, 20, 20));
            this.pictureBox1.MouseDown += new MouseEventHandler(Title_MouseDown);
            this.pictureBox1.MouseUp += new MouseEventHandler(Title_MouseUp);
            this.pictureBox1.MouseMove += new MouseEventHandler(Title_MouseMove);
            label1.Text = "User " + this.userId;
            label2.Text = "Blocked ?";
            label3.Text = "Plan :";
        }


        void Title_MouseUp(object sender, MouseEventArgs e)
        {
            this.drag = false;
        }

        void Title_MouseDown(object sender, MouseEventArgs e)
        {
            this.startPoint = e.Location;
            this.drag = true;
        }

        void Title_MouseMove(object sender, MouseEventArgs e)
        {
            if (this.drag)
            {
                Point p1 = new Point(e.X, e.Y);
                Point p2 = this.PointToScreen(p1);
                Point p3 = new Point(p2.X - this.startPoint.X,
                                     p2.Y - this.startPoint.Y);
                this.Location = p3;
            }
        }

        private void ModifForm_Load(object sender, EventArgs e)
        {

        }

     

        private void cyberButton2_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.Cancel;
            
            Close();
        }

        private void cyberButton1_Click(object sender, EventArgs e)
        {
            DialogResult=DialogResult.OK;

            ParrotSwitch.State a = parrotSwitch1.SwitchState;

            this.isBlocked = a==ParrotSwitch.State.On ? true : false;
            this.plan = (string)crownComboBox1.SelectedItem;
            this.Close();

        }

        private void crownComboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {

        }
    }
}
