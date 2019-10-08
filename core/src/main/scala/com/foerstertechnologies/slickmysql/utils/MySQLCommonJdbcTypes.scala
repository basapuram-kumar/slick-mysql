package com.foerstertechnologies.slickmysql.utils

import slick.ast.FieldSymbol

import scala.reflect.ClassTag
import java.sql.{PreparedStatement, ResultSet}

import slick.jdbc.{JdbcTypesComponent, MySQLProfile}

/**
  * @
  */
trait MySQLCommonJdbcTypes extends JdbcTypesComponent { driver: MySQLProfile =>

  class GenericJdbcType[T](val sqlTypeName: String,
                           fnFromString: (String => T),
                           fnToString: (T => String) = ((r: T) => r.toString),
                           val sqlType: Int = java.sql.Types.OTHER,
                           override val hasLiteralForm: Boolean = false)(
                  implicit override val classTag: ClassTag[T]) extends DriverJdbcType[T] {

    override def sqlTypeName(sym: Option[FieldSymbol]): String = sqlTypeName

    override def getValue(r: ResultSet, idx: Int): T = {
      val value = r.getString(idx)
      if (r.wasNull) null.asInstanceOf[T] else fnFromString(value)
    }

    override def setValue(v: T, p: PreparedStatement, idx: Int): Unit = p.setObject(idx, toStr(v), sqlType)

    override def updateValue(v: T, r: ResultSet, idx: Int): Unit = r.updateObject(idx, toStr(v), sqlType)

    override def valueToSQLLiteral(v: T) = if(v == null) "NULL" else s"'${fnToString(v)}'"

    ///
    private def toStr(v: T) = if(v == null) null else fnToString(v)
  }
}
