/*
* Copyright (C) 2018 SwiftLeap.com, Ruan Strydom
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
  
package org.swiftleap.common.comms.impl

import org.junit.Assert
import org.junit.Test
import org.swiftleap.common.comms.impl.BasicHostSolver

class BasicHostSolverTest {

    @Test
    fun solve() {
        val solver = BasicHostSolver()

        val solvedRr = solver.parse("@rr http://host1:81/api/v1 http://host2:82/api/v1")
        val hostRr1 = solver.solve(solvedRr)
        val hostRr2 = solver.solve(solvedRr)
        val hostRr3 = solver.solve(solvedRr)
        Assert.assertNotEquals(hostRr1, hostRr2)
        Assert.assertEquals(hostRr1, hostRr3)

        val solved = solver.parse("host1 host2 host3")
        val host1 = solver.solve(solved)
        val host2 = solver.solve(solved)
        Assert.assertEquals(host1, host2)
    }

    @Test
    fun badHost() {
        val solver = BasicHostSolver()
        val solved = solver.parse("host1 host2 host3")
        val host1 = solver.solve(solved)
        val host2 = solver.solve(solved)
        Assert.assertEquals(host1, host2)

        solver.badHost(solved, host1)
        val host3 = solver.solve(solved)
        Assert.assertNotEquals(host1, host3)
    }

    @Test
    fun goodHost() {
        val solver = BasicHostSolver()
        val solved = solver.parse("host1 host2 host3")
        val host1 = solver.solve(solved)
        val host2 = solver.solve(solved)
        Assert.assertEquals(host1, host2)

        solver.badHost(solved, host1)
        solver.goodHost(solved, host1)
        val host3 = solver.solve(solved)
        Assert.assertEquals(host1, host3)
    }

    @Test
    fun hasGoodHost() {
        val solver = BasicHostSolver()
        val solved = solver.parse("host1")
        val host1 = solver.solve(solved)
        solver.badHost(solved, host1)
        Assert.assertFalse(solver.hasGoodHost(solved))
    }

    @Test
    fun parse() {
        val solver = BasicHostSolver()
        Assert.assertTrue(solver.parse("@rr http://host1:81/api/v1 http://host2:82/api/v1").isValid())
        Assert.assertTrue(solver.parse("host1").isValid())
        Assert.assertFalse(solver.parse("   ").isValid())
    }
}
