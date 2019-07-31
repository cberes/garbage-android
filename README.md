# Garbo Clock

Never forget trash day again! Garbo Clock reminds you when it's garbage day.
Choose your town, your garbage or recycling schedule (if applicable), and when you wish to receive notifications.
Then Garbo Clock will notify you at your desired time when trash day arrives.

## Locations supported

Currently, only locations in Erie County, New York, are supported.

## Add your town

Adding new towns to Garbo Clock is intended to be easy.

Towns and garbage configurations are listed in [data.json](/app/src/main/res/raw/data.json).

### Municipality preset

First, look for a configuration that meets your needs. If you find one, then just add a new entry to the `presets` array.

```json
{
  "id": "2b449a5d-244e-4a0c-bd82-562ad5340db1",
  "name": "Buffalo",
  "url": "https://www.buffalony.gov/382/Streets-Sanitation",
  "configurationId": "138ffe14-20ac-4152-b0a6-cfbc54c33dcd"
}
```

- `id` must be a unique ID (preferably a UUID)
- `name` is the municipality's name
- `url` is a URL that points to information about the town's garbage collection
- `configurationId` refers to the `id` field of an element of the `configurations` array

### Garbage configuration

If no suitable configuration is found, you must first add one to the `configurations` array.

```json
{
  "id": "b87cb3a7-b9b9-462a-8d45-ebd6eb056aa5",
  "note": "Towns that have alternate recycling that start 2019 on A",
  "start": "2018-12-30",
  "reset": "SUNDAY",
  "items": [
    {
      "item": "GARBAGE",
      "enabled": true
    },
    {
      "item": "RECYCLING",
      "enabled": true,
      "weeks": [
        "A",
        "B"
      ]
    }
  ],
  "bulkDays": [
    "2019-04-08",
    "2019-09-16"
  ],
  "leapDays": [
    "2019-01-01",
    "2019-05-27",
    "2019-07-04",
    "2019-09-02",
    "2019-11-28",
    "2020-01-01"
  ],
  "holidays": [
    "2019-12-25"
  ]
}
```

- `id` must be a unique ID (preferably a UUID)
- `note` is a short description of the configurtions
- `start` is the start date of the schedule in the format of `YYYY-MM-DD`
  - If collection is not weekly, then this should be the first day of the first week(s) in the `items` array
- `reset` is the first day of the garbage collection for the week
  - See `leapDays` for more information
  - Use `SUNDAY` if you're unsure
- `items` list the types of collection that are performed (e.g. garbage, recycling, etc.)
  - `item` must be either `GARBAGE` or `RECYCLING`
  - `enabled` is `true` if this type of collection occurs, and `false` otherwise
  - `weeks` is a list of week options (omit if collection occurs weekly)
- `bulkDays` is a list of first days of weeks when bulk trash is collected for all residents
  - This field is optional. Please omit it if bulk trash is not collected.
- `leapDays` are days when collection does not occur
  - Instead, collection will occur one day later for residents
  - Collection days are reset to normal when the next `reset` is encountered
  - If the leap day falls on the `reset` day, then collection will occur one day later for the entire week
- `holidays` are days when collection does not occur
  - Unlike `leapDays`, `holidays` do not affect following days. Simply there is no collection on a holiday.

#### Weeks

Including two week names indicated that collection is biweekly.
Collection for some customers will occur during week 1.
For the other customers, collection will be during week 2.

Add additional entries if collection occurs less frequently.

The `start` date must be the first day of the week (see `reset`) for the first week listed.
In the example above, customers on week A will have their recycling collected from December 30 through January 5.
Customers on week B will have their recycling collected from January 6 through January 12.

#### Biweekly collection

If collection is biweekly such that all residents' trash is collected on one week and no collection is down the next week, include `(disabled)` weeks.
Garbo Clock knows to ignore these entries.

```json
{
  "item": "RECYCLING",
  "enabled": true,
  "weeks": [
    "Biweekly",
    "(disabled)"
  ]
}
```

The first element _must not_ be `(disabled)`.

You can add an additional `(disabled)` element for triweekly collection, and so on.
