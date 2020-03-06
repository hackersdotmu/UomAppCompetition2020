﻿using Xamarin.Plugin.Calendar.Models;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Globalization;
using System.Runtime.CompilerServices;
using System.Threading.Tasks;
using System.Linq;
using System.Collections.ObjectModel;
using System.Windows.Input;
using Xamarin.Forms;
using NoPoverty.Helper;
using NoPoverty.Models;

namespace NoPoverty.Views
{
    public class MainPageViewModel : INotifyPropertyChanged
    {
        public ICommand DayTappedCommand => new Command<DateTime>(DayTapped);
        FirebaseCalendar fc = new FirebaseCalendar();
        EventModel current = new EventModel();
        public MainPageViewModel()
        {
            Culture = CultureInfo.CreateSpecificCulture("en-US");

            // testing all kinds of adding events
            // when initializing collection
            Events = new EventCollection();


            //Add Event
            AddEvent2("05/03/2020 08:00:34", "Reeshal", "Meet infront of Cafetaria");
            AddEvent2("05/03/2020 18:00:34", "Hevin ", "Meeting pt: behind supermarket");
            AddEvent2("07/03/2020 10:00:34", "Yadav", "Bring a large bag");
            AddEvent2("06/03/2020 06:00:34", "lel", "ok");
            AddEvent2("03/18/2020 16:00:34", "lol", "owkay");
            //DeleteEvent("05/03/2020 18:00:34", "Waaaooooa xD");



        }
        
       


        protected IEnumerable<EventModel> AddEvents(string datetime, string name, string desc)
        {
            return Enumerable.Range(1, 1).Select(x => new EventModel
            {
                DateTime = $"{datetime}",
                DonorUserName = $"{name}",
                Description = $"{desc}"
            });
        }
        public void AddEvent(string datetime, string name, string desc)
        {
            DateTime dt = new DateTime();
            dt = DateTime.Parse(datetime);

            Events.Add(dt, new List<EventModel>(AddEvents(dt.ToShortTimeString(), name, desc)));
        }

        public void AddEvent2(string datetime, string name, string desc)
        {
            Task.Delay(5000).ContinueWith(_ =>
            {
                DateTime dt = new DateTime();
                dt = DateTime.Parse(datetime);
                // indexer - update later
                if (!Events.ContainsKey(dt))
                {

                    Events[dt] = new ObservableCollection<EventModel>(AddEvents(dt.ToShortTimeString(), name, desc));

                }
                else
                {
                    Task.Delay(3000).ContinueWith(t =>
                    {

                        // get observable collection later
                        var currentEvents = Events[dt] as ObservableCollection<EventModel>;
                        currentEvents.Add(new EventModel { DateTime = $"{dt.ToShortTimeString()}", DonorUserName = $"{name}", Description = $"{desc}" });
                    });
                }
            }, TaskScheduler.FromCurrentSynchronizationContext());

        }

        public void DeleteEvent(string datetime, string name)

        {
            Task.Delay(5000).ContinueWith(_ =>
            {
                DateTime dt = new DateTime();
                dt = DateTime.Parse(datetime);
                // indexer - update later
                if (Events.ContainsKey(dt))
                {
                    Task.Delay(3000).ContinueWith(t =>
                    {
                        // get observable collection later
                        var currentEvents = Events[dt] as ObservableCollection<EventModel>;
                        currentEvents.Remove(currentEvents.Where(i => i.DonorUserName == name).Single());
                    });
                }
            }, TaskScheduler.FromCurrentSynchronizationContext());
        }

        public EventCollection Events { get; }
        public int Month { get; set; } = DateTime.Now.Month;
        public int Year { get; set; } = DateTime.Now.Year;

        private DateTime _selectedDate = DateTime.Today;
        public DateTime SelectedDate
        {
            get => _selectedDate;
            set => SetProperty(ref _selectedDate, value);
        }

        private DateTime _minimumDate = new DateTime(2019, 4, 29);
        public DateTime MinimumDate
        {
            get => _minimumDate;
            set => SetProperty(ref _minimumDate, value);
        }

        private DateTime _maximumDate = DateTime.Today.AddMonths(5);
        public DateTime MaximumDate
        {
            get => _maximumDate;
            set => SetProperty(ref _maximumDate, value);
        }

        private CultureInfo _culture = CultureInfo.InvariantCulture;
        public CultureInfo Culture
        {
            get => _culture;
            set => SetProperty(ref _culture, value);
        }

        private static void DayTapped(DateTime date)
        {
            Console.WriteLine($"Received tap event from date: {date}");
        }

        #region INotifyPropertyChanged

        public event PropertyChangedEventHandler PropertyChanged;

        private void SetProperty<TData>(ref TData storage, TData value, [CallerMemberName] string propertyName = "")
        {
            if (storage.Equals(value))
                return;

            storage = value;

            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }

        #endregion

    }
}
