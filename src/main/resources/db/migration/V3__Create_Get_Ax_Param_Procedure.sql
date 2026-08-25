-- -----------------------------------------------------
-- Migration: Create Procedure sp_get_ax_param
-- Description: Replaces Oracle FUNCTION GET_AX_PARAM
-- -----------------------------------------------------

DELIMITER //

DROP PROCEDURE IF EXISTS sp_get_ax_param //

CREATE PROCEDURE sp_get_ax_param(
    IN p_ax VARCHAR(255), 
    IN p_code VARCHAR(255)
)
BEGIN
    -- In MySQL, a simple SELECT inside a procedure 
    -- acts as the RETURN SYS_REFCURSOR from Oracle.
    SELECT 
        id, 
        ax, 
        codes 
    FROM 
        ax_v2
    WHERE 
        ax = p_ax 
        AND codes = p_code;
END //

DELIMITER ;