package com.mgslira.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mgslira.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}
