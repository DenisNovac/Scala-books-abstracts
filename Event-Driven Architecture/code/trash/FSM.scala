import cats.syntax.all.*
import cats.{Functor, Id}

case class FSM[F[_], S, I, O](run: (S, I) => F[(S, O)]):
  def runS(using F: Functor[F]): (S, I) => F[S] =
    (s, i) => run(s, i).map(_._1)

object FSM:
  def id[S, I, O](run: (S, I) => Id[(S, O)]) = FSM(run)

object TradeEngine:
  val fsm =
    FSM.id[
      TradeState,
      TradeCommand | SwitchCommand,
      (EventId, Timestamp) => TradeEvent | SwitchEvent
    ] {
      // Trading status: On
      case (st @ TradeState(On, _), cmd @ Create(_, cid, sl, ac, p, q, _, _)) =>
        val nst = st.modify(sl)(ac, p, q)
        nst -> ((id, ts) => CommandExecuted(id, cid, cmd, ts))
      case (st @ TradeState(On, _), cmd @ Update(_, cid, sl, ac, p, q, _, _)) =>
        val nst = st.modify(sl)(ac, p, q)
        nst -> ((id, ts) => CommandExecuted(id, cid, cmd, ts))
      case (st @ TradeState(On, _), cmd @ Delete(_, cid, sl, ac, p, _, _))    =>
        val nst = st.remove(sl)(ac, p)
        nst -> ((id, ts) => CommandExecuted(id, cid, cmd, ts))
      // Trading status: Off
      case (st @ TradeState(Off, _), cmd: TradeCommand)                       =>
        val rs = Reason("Trading is off")
        st -> ((id, ts) => CommandRejected(id, cmd.cid, cmd, rs, ts))
      // Trading switch: On / Off
      case (st @ TradeState(Off, _), Start(_, cid, _))                        =>
        val nst = TradeState._Status.replace(On)(st)
        nst -> ((id, ts) => Started(id, cid, ts))
      case (st @ TradeState(On, _), Stop(_, cid, _))                          =>
        val nst = TradeState._Status.replace(Off)(st)
        nst -> ((id, ts) => Stopped(id, cid, ts))
      case (st @ TradeState(On, _), Start(_, cid, _))                         =>
        st -> ((id, ts) => Ignored(id, cid, ts))
      case (st @ TradeState(Off, _), Stop(_, cid, _))                         =>
        st -> ((id, ts) => Ignored(id, cid, ts))
    }
