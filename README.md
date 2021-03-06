# slick-mysql
Slick extensions for MySQL8

This package is heavly based on 
[slick-pg](https://github.com/tminglei/slick-pg) which brings addional support for different data structures to slick.

Currently the package supports
 * [Spatial Data Types](https://dev.mysql.com/doc/refman/8.0/en/spatial-types.html) features that came along with MySQL 8.
 * Play Json values


# Spatial extension
This package is working with `org.locationtech.jts » jts-core` instead of `com.vividsolutions » jts-core` 

## Installation

Play 2.7 / Scala 2.11 or 2.12
```
libraryDependencies += "com.foerster-technologies" %% "slick-mysql" % "0.6.0-SNAPSHOT
```

Play 2.8 / Scala 2.12 or 2.13
```
libraryDependencies += "com.foerster-technologies" %% "slick-mysql" % "0.6.0-SNAPSHOT
```

### Addons

> JTS Support
```
libraryDependencies += "com.foerster-technologies" %% "slick-mysql_jts" % "0.6.0-SNAPSHOT
```

> Play Json
```
libraryDependencies += "com.foerster-technologies" %% "slick-mysql_play-json" % "0.6.0-SNAPSHOT
```

## Usage

### Create your own Slick-Profile
this example includes the slick mysql profile with the spatial extension
```
package your.package

import com.foerstertechnologies.slickmysql.{ExMySQLProfile, MySQLSpatialSupport}

trait MySQLSpatialProfile extends ExMySQLProfile
  with MySQLSpatialSupport {

  override val api = new MyAPI {}

  trait MyAPI extends API
    with MySQLSpatialImplicits with MySQLSpatialAssistants
}

object MySQLSpatialProfile extends MySQLSpatialProfile
```

You can also use `com.foerstertechnologies.slickmysql.MySQLSpatialProfile` which comes with the JTS package.
### Register your profile

For slick play: Set your own profile in your config
```
slick.dbs.default.profile = "your.package.MySQLSpatialProfile$"
```


### In your code

Gps example with JTS
```
import your.package.MySQLSpatialProfile
import play.api.db.slick.HasDatabaseConfigProvider
import org.locationtech.jts.geom.Point

case class Test(id : Long, gps : Point)

class TestTable(tag: Tag) extends Table[Test](tag, Some("xxx"), "Test") {
  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
  def gps = column[Point]("location")

  def * = (id, gps) <> (Test.tupled, Test.unapply)
}


trait MyTestClass extends HasDatabaseConfigProvider[MySQLSpatialProfile]
{
    import profile.api._
    
    def findAroundPoint(position: Point, limit : Int) : DBIO[Seq[Test] = {
        TestTable.sortBy(_.gps.distanceSphere(position)).take(limit).result
    }
}

```

## Documentation

### Stored SRID Values
The Geometry values are stored and loaded with the given SRID.   
So make sure to add SRID to your value when you save it.
